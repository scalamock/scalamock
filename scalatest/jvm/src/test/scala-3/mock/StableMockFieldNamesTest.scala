package mock

import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import java.io.DataInputStream

trait OneMethod {
  def keep(x: Int): String
}

trait ManyMethods {
  def a1(): Unit
  def a2(x: Int): Int
  def a3(x: String): String
  def a4(): Boolean
  def a5(x: Long): Long
  def a6(): Unit
  def a7(x: Int): Int
  def a8(x: String): String
  def keep(x: Int): String
  def a9(): Boolean
  def a10(x: Long): Long
  def a11(): Unit
  def a12(x: Int): Int
  def a13(x: String): String
  def a14(): Boolean
  def a15(x: Long): Long
  def a16(): Unit
}

trait Overloads {
  def keep(x: Int): String
  def keep(x: String): String
}

// "Aa" and "BB" have the same String.hashCode, so these signatures collide under a hash of the signature.
class Aa
class BB

trait CollidingOverloads {
  def keep(x: Aa): String
  def keep(x: BB): String
}

trait InternalLookingMemberNames {
  def `macro`(): String
  def special(): String
}

class StableMockFieldNamesTest extends AnyFunSpec with MockFactory with Matchers {

  private def fieldsFor(method: String, m: AnyRef): List[String] =
    m.getClass.getDeclaredFields.toList.map(_.getName).filter(_.startsWith(s"mock$$$method$$")).sorted

  // Read the class-file layout directly: reflection does not promise declaration order.
  private def classFileFields(value: AnyRef): List[String] = {
    val resource = "/" + value.getClass.getName.replace('.', '/') + ".class"
    val stream = Option(value.getClass.getResourceAsStream(resource))
      .getOrElse(fail(s"Missing generated class resource: $resource"))
    val input = new DataInputStream(stream)
    def skipFully(length: Long): Unit = {
      var remaining = length
      while (remaining > 0) {
        val skipped = input.skipBytes(math.min(remaining, Int.MaxValue.toLong).toInt)
        if (skipped > 0) remaining -= skipped
        else {
          input.readByte() // Throws EOFException rather than looping on a truncated attribute.
          remaining -= 1
        }
      }
    }
    try {
      require(input.readInt() == 0xcafebabe, s"Invalid class-file magic in $resource")
      input.readUnsignedShort() // minor_version
      input.readUnsignedShort() // major_version
      val constants = new Array[String](input.readUnsignedShort())
      var index = 1
      while (index < constants.length) {
        input.readUnsignedByte() match {
          case 1 => constants(index) = input.readUTF()
          case 3 | 4 => input.readInt() // Integer or Float bits
          case 5 | 6 => input.readLong(); index += 1 // Long/Double occupy two pool entries.
          case 7 | 8 | 16 | 19 | 20 => input.readUnsignedShort() // One pool index
          case 9 | 10 | 11 | 12 | 17 | 18 => // Two pool/bootstrap indexes
            input.readUnsignedShort()
            input.readUnsignedShort()
          case 15 => // MethodHandle: reference_kind, reference_index
            input.readUnsignedByte()
            input.readUnsignedShort()
          case tag => fail(s"Unexpected class-file constant tag: $tag")
        }
        index += 1
      }
      input.readUnsignedShort() // access_flags
      input.readUnsignedShort() // this_class
      input.readUnsignedShort() // super_class
      for (_ <- 0 until input.readUnsignedShort()) input.readUnsignedShort() // interfaces
      List.fill(input.readUnsignedShort()) {
        input.readUnsignedShort() // access_flags
        val name = constants(input.readUnsignedShort())
        input.readUnsignedShort() // descriptor_index
        for (_ <- 0 until input.readUnsignedShort()) {
          input.readUnsignedShort() // attribute_name_index
          val attributeLength = java.lang.Integer.toUnsignedLong(input.readInt())
          skipFully(attributeLength)
        }
        name
      }
    } finally input.close()
  }

  private def generatedFields(value: AnyRef, prefix: String): List[String] =
    classFileFields(value).filter(name => name.startsWith(prefix) && name != "mock$special$mockName")

  it("includes user members with internal-looking names") {
    generatedFields(mock[InternalLookingMemberNames], "mock$") shouldBe List("mock$macro$0", "mock$special$0")
  }

  private val expectedMemberKeys = List("alpha$0", "alpha$1", "collide$0", "collide$1",
    "deferredReference$0", "inherited$0", "inheritedReference$0", "zeta$0")

  it("emits classic mock fields in deterministic name and overload order") {
    generatedFields(mock[DeterministicEmissionFixtures.Service], "mock$") shouldBe
      expectedMemberKeys.map("mock$" + _)
  }

  it("emits classic stub fields in the same deterministic order") {
    generatedFields(stub[DeterministicEmissionFixtures.Service], "mock$") shouldBe
      expectedMemberKeys.map("mock$" + _)
  }

  it("emits new stub fields in the same deterministic order") {
    class Factory extends org.scalamock.stubs.Stubs {
      def instance: AnyRef = stub[DeterministicEmissionFixtures.Service].asInstanceOf[AnyRef]
    }
    val factory = new Factory
    generatedFields(factory.instance, "stub$") shouldBe expectedMemberKeys.map("stub$" + _)
  }

  // A mock class and an expectation that refers to it can come from different compiler runs
  // (incremental compilation, or a mock made in one module and used in another). Both must
  // derive the same field name, so it must not depend on the type's other members.
  it("names a method's mock function by the method alone, not its position among the members") {
    val oneMethod = fieldsFor("keep", mock[OneMethod])
    oneMethod should have size 1
    fieldsFor("keep", mock[ManyMethods]) shouldBe oneMethod
  }

  it("gives each overload its own mock function") {
    fieldsFor("keep", mock[Overloads]) shouldBe List("mock$keep$0", "mock$keep$1")
  }

  it("keeps overloads apart even when their signatures hash alike") {
    val m = mock[CollidingOverloads]
    fieldsFor("keep", m) should have size 2
    (m.keep(_: Aa)).expects(*).returning("aa")
    (m.keep(_: BB)).expects(*).returning("bb")
    m.keep(new Aa) shouldBe "aa"
    m.keep(new BB) shouldBe "bb"
  }

  it("still finds each overload's mock function from an expectation") {
    val m = mock[Overloads]
    (m.keep(_: Int)).expects(17).returning("int")
    (m.keep(_: String)).expects("s").returning("string")
    m.keep(17) shouldBe "int"
    m.keep("s") shouldBe "string"
  }
}
