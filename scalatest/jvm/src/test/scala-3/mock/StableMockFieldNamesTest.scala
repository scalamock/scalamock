package mock

import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

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

class StableMockFieldNamesTest extends AnyFunSpec with MockFactory with Matchers {

  private def fieldsFor(method: String, m: AnyRef): List[String] =
    m.getClass.getDeclaredFields.toList.map(_.getName).filter(_.startsWith(s"mock$$$method$$")).sorted

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
