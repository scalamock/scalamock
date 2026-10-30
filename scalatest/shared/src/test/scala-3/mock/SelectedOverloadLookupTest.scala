// Copyright (c) ScalaMock Contributors (https://github.com/ScalaMock/ScalaMock/graphs/contributors)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package mock

import org.scalamock.scalatest.MockFactory
import org.scalamock.stubs.{Stub, Stubs}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.compiletime.testing.typeCheckErrors
import scala.concurrent.Future

object SelectedOverloadFixtures {
  class Wide
  class Narrow extends Wide

  // The same bounded overloads in both declaration orders: lookup must not depend on member order.
  // Direct foo[Narrow] calls are ascribed `: String`; as the receiver of `shouldBe`, Scala 3
  // cannot choose between the bounded overloads.
  trait WideBoundFirst {
    def foo[T <: Wide](value: T): String
    def foo[T <: Narrow](value: T): String
  }

  trait NarrowBoundFirst {
    def foo[T <: Narrow](value: T): String
    def foo[T <: Wide](value: T): String
  }

  trait RelatedOverloads {
    def choose(value: CharSequence): String
    def choose(value: String): String
  }

  class FinalNarrowOverload {
    final def pick(value: String): String = "final"
    def pick(value: CharSequence): String = "open"
  }

  // Each function below compiles but omits or widens parameters of the selected method.
  trait SignatureShapes {
    def withUsing(value: Int)(using String): String
    def withDefault(value: Int, other: Int = 2): String
    def twoArgs(value: Int, other: Int): String
    def widened(value: Long): String
    def fetch(value: Int): Future[Int]
  }

  trait OpaqueBase {
    def f(value: Int): String
  }

  // f(String) sorts before f(Int), so binding a sibling instead of the override would be visible.
  trait OpaqueDerived extends OpaqueBase {
    override def f(value: Int): String
    def f(value: String): String
  }

  // Outside this object, Wrapped selects OpaqueBase.f while the mocked class overrides it.
  object OpaqueReceivers {
    opaque type Wrapped <: OpaqueBase = OpaqueDerived
    def wrap(value: OpaqueDerived): Wrapped = value
    def wrapStub(value: Stub[OpaqueDerived]): Stub[Wrapped] = value
  }

  trait DiamondLeft {
    def f(value: Int): String
    def h[T](value: T): String
  }

  trait DiamondRight {
    def f(value: Int): String
    def h[T](value: T): String
  }

  // DiamondRight's members implement DiamondLeft's without overriding them; f(String) sorts first.
  trait DiamondBoth extends DiamondLeft with DiamondRight {
    def f(value: String): String
  }

  // Outside this object, Wrapped selects DiamondLeft's members while the mocked class lists DiamondRight's.
  object DiamondReceivers {
    opaque type Wrapped <: DiamondLeft = DiamondBoth
    def wrap(value: DiamondBoth): Wrapped = value
    def wrapStub(value: Stub[DiamondBoth]): Stub[Wrapped] = value
  }

  trait GenericLeft[T] {
    def g(value: T): String
  }

  trait IntRight {
    def g(value: Int): String
  }

  // GenericLeft[Int].g erases to g(Object) and IntRight.g to g(int), yet both are the same member here.
  trait GenericBoth extends GenericLeft[Int] with IntRight {
    def g(value: String): String
  }

  object GenericDiamondReceivers {
    opaque type Wrapped <: GenericLeft[Int] = GenericBoth
    def wrap(value: GenericBoth): Wrapped = value
    def wrapStub(value: Stub[GenericBoth]): Stub[Wrapped] = value
  }

  // Legal overloads: the parameters erase alike, the results differ.
  trait ErasedTwins {
    def f(value: List[Int]): Int
    def f(value: List[String]): String
  }

  trait TwinLeft {
    def t(value: List[Int]): Int
  }

  trait TwinRight {
    def t(value: List[Int]): Int
  }

  // t(List[String]) erases like the implementation of TwinLeft.t but is a sibling overload.
  trait TwinBoth extends TwinLeft with TwinRight {
    def t(value: List[String]): String
  }

  object TwinReceivers {
    opaque type Wrapped <: TwinLeft = TwinBoth
    def wrap(value: TwinBoth): Wrapped = value
    def wrapStub(value: Stub[TwinBoth]): Stub[Wrapped] = value
  }

  trait PolyLeft[T] {
    def h[U <: T](value: U): String
  }

  trait PolyRight {
    def h[U <: Int](value: U): String
  }

  // PolyLeft[Int].h and PolyRight.h erase differently; through the alias, lookup matches by argument types.
  // No sibling overload here: through the alias, the bound's members would number it differently
  // from the mocked class, the separate supertype index drift that also fails on series/7.x.
  trait PolyBoth extends PolyLeft[Int] with PolyRight

  object PolyReceivers {
    opaque type Wrapped <: PolyLeft[Int] = PolyBoth
    def wrap(value: PolyBoth): Wrapped = value
    def wrapStub(value: Stub[PolyBoth]): Stub[Wrapped] = value
  }

  trait Command {
    type Answer
  }

  class IntCommand extends Command {
    type Answer = Int
  }

  // Both parents declare a dependent and a higher-kinded method; the receiver's class lists one of each.
  trait SignatureLeft {
    def dep[T <: Command](cmd: T)(x: cmd.Answer): String
    def hk[A, F[X] <: Either[A, X]](a: F[Int]): String
  }

  trait SignatureRight {
    def dep[T <: Command](cmd: T)(x: cmd.Answer): String
    def hk[A, F[X] <: Either[A, X]](a: F[Int]): String
  }

  trait SignatureBoth extends SignatureLeft with SignatureRight

  object SignatureReceivers {
    opaque type Wrapped <: SignatureLeft = SignatureBoth
    def wrap(value: SignatureBoth): Wrapped = value
    def wrapStub(value: Stub[SignatureBoth]): Stub[Wrapped] = value
  }
}

class SelectedOverloadLookupTest extends AnyFunSpec with MockFactory with Matchers {
  import SelectedOverloadFixtures.*

  it("binds classic mock expectations to the selected bounded generic overload") {
    val wideFirst = mock[WideBoundFirst]
    (wideFirst.foo[Wide](_: Wide)).expects(*).returning("wide")
    (wideFirst.foo[Narrow](_: Narrow)).expects(*).returning("narrow")
    wideFirst.foo[Wide](new Wide) shouldBe "wide"
    (wideFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"

    val narrowFirst = mock[NarrowBoundFirst]
    (narrowFirst.foo[Wide](_: Wide)).expects(*).returning("wide")
    (narrowFirst.foo[Narrow](_: Narrow)).expects(*).returning("narrow")
    narrowFirst.foo[Wide](new Wide) shouldBe "wide"
    (narrowFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
  }

  it("binds classic stub expectations to the selected bounded generic overload") {
    val wideFirst = stub[WideBoundFirst]
    (wideFirst.foo[Wide](_: Wide)).when(*).returns("wide")
    (wideFirst.foo[Narrow](_: Narrow)).when(*).returns("narrow")
    wideFirst.foo[Wide](new Wide) shouldBe "wide"
    (wideFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"

    val narrowFirst = stub[NarrowBoundFirst]
    (narrowFirst.foo[Wide](_: Wide)).when(*).returns("wide")
    (narrowFirst.foo[Narrow](_: Narrow)).when(*).returns("narrow")
    narrowFirst.foo[Wide](new Wide) shouldBe "wide"
    (narrowFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
  }

  it("binds a classic mock expectation to the selected overload when its argument is narrower") {
    val value = mock[RelatedOverloads]
    (value.choose(_: CharSequence)).expects("wide").returning("sequence")
    (value.choose(_: String)).expects("narrow").returning("string")
    value.choose("wide": CharSequence) shouldBe "sequence"
    value.choose("narrow") shouldBe "string"
  }

  it("binds a classic stub expectation to the selected overload when its argument is narrower") {
    val value = stub[RelatedOverloads]
    (value.choose(_: CharSequence)).when("wide").returns("sequence")
    (value.choose(_: String)).when("narrow").returns("string")
    value.choose("wide": CharSequence) shouldBe "sequence"
    value.choose("narrow") shouldBe "string"
  }

  it("rejects a classic mock expectation on a final overload instead of binding its sibling") {
    val value = mock[FinalNarrowOverload]
    "(value.pick(_: String)).expects(\"narrow\")" shouldNot compile
    typeCheckErrors("(value.pick(_: String)).expects(\"narrow\")").map(_.message).mkString("\n") should
      include("is unavailable for mocking: it is final")
    (value.pick(_: CharSequence)).expects("wide").returning("open mock")
    value.pick("wide": CharSequence) shouldBe "open mock"
  }

  it("rejects a classic stub expectation on a final overload instead of binding its sibling") {
    val value = stub[FinalNarrowOverload]
    "(value.pick(_: String)).when(\"narrow\")" shouldNot compile
    typeCheckErrors("(value.pick(_: String)).when(\"narrow\")").map(_.message).mkString("\n") should
      include("is unavailable for mocking: it is final")
    (value.pick(_: CharSequence)).when("wide").returns("open stub")
    value.pick("wide": CharSequence) shouldBe "open stub"
  }

  it("binds a classic mock expectation through an opaque receiver to the class's override") {
    val derived = mock[OpaqueDerived]
    val value = OpaqueReceivers.wrap(derived)
    (value.f(_: Int)).expects(1).returning("override")
    (derived.f(_: String)).expects("s").returning("sibling")
    value.f(1) shouldBe "override"
    derived.f("s") shouldBe "sibling"
  }

  it("binds a classic stub expectation through an opaque receiver to the class's override") {
    val derived = stub[OpaqueDerived]
    val value = OpaqueReceivers.wrap(derived)
    (value.f(_: Int)).when(1).returns("override")
    (derived.f(_: String)).when("s").returns("sibling")
    value.f(1) shouldBe "override"
    derived.f("s") shouldBe "sibling"
  }

  it("binds a classic mock expectation through an opaque receiver to a member from another parent") {
    val derived = mock[DiamondBoth]
    val value = DiamondReceivers.wrap(derived)
    (value.f(_: Int)).expects(1).returning("implementation")
    (derived.f(_: String)).expects("s").returning("sibling")
    (value.h[Int](_: Int)).expects(2).returning("generic")
    value.f(1) shouldBe "implementation"
    derived.f("s") shouldBe "sibling"
    value.h[Int](2) shouldBe "generic"
  }

  it("binds a classic mock expectation through an opaque receiver to a member from a generic parent") {
    val derived = mock[GenericBoth]
    val value = GenericDiamondReceivers.wrap(derived)
    (value.g(_: Int)).expects(1).returning("implementation")
    (derived.g(_: String)).expects("s").returning("sibling")
    value.g(1) shouldBe "implementation"
    derived.g("s") shouldBe "sibling"
  }

  it("keeps classic mock overloads with the same erased parameters apart") {
    val value = mock[ErasedTwins]
    (value.f(_: List[Int])).expects(List(1)).returning(1)
    (value.f(_: List[String])).expects(List("a")).returning("a")
    value.f(List(1)) shouldBe 1
    value.f(List("a")) shouldBe "a"
  }

  it("keeps classic stub overloads with the same erased parameters apart") {
    val value = stub[ErasedTwins]
    (value.f(_: List[Int])).when(List(1)).returns(1)
    (value.f(_: List[String])).when(List("a")).returns("a")
    value.f(List(1)) shouldBe 1
    value.f(List("a")) shouldBe "a"
  }

  it("binds a classic mock expectation through an opaque receiver past an erased-twin sibling") {
    val derived = mock[TwinBoth]
    val value = TwinReceivers.wrap(derived)
    (value.t(_: List[Int])).expects(List(1)).returning(1)
    (derived.t(_: List[String])).expects(List("a")).returning("a")
    value.t(List(1)) shouldBe 1
    derived.t(List("a")) shouldBe "a"
  }

  it("binds a classic mock expectation through an opaque receiver to an aligned polymorphic member") {
    val derived = mock[PolyBoth]
    val value = PolyReceivers.wrap(derived)
    (value.h[Int](_: Int)).expects(1).returning("int").twice()
    value.h[Int](1) shouldBe "int"
    derived.h[Int](1) shouldBe "int"
  }

  it("binds a classic stub expectation through an opaque receiver to an aligned polymorphic member") {
    val derived = stub[PolyBoth]
    val value = PolyReceivers.wrap(derived)
    (value.h[Int](_: Int)).when(1).returns("int")
    value.h[Int](1) shouldBe "int"
    derived.h[Int](1) shouldBe "int"
  }

  it("binds classic mock expectations through an opaque receiver to dependent and higher-kinded members") {
    val value = SignatureReceivers.wrap(mock[SignatureBoth])
    (value.dep[IntCommand](_: IntCommand)(_: Int)).expects(*, 5).returning("dep")
    (value.hk[String, [X] =>> Either[String, X]](_: Either[String, Int])).expects(*).returning("hk")
    value.dep(new IntCommand)(5) shouldBe "dep"
    value.hk[String, [X] =>> Either[String, X]](scala.util.Right(5)) shouldBe "hk"
  }

  it("binds a classic stub expectation through an opaque receiver to a member from another parent") {
    val derived = stub[DiamondBoth]
    val value = DiamondReceivers.wrap(derived)
    (value.f(_: Int)).when(1).returns("implementation")
    (derived.f(_: String)).when("s").returns("sibling")
    value.f(1) shouldBe "implementation"
    derived.f("s") shouldBe "sibling"
  }

  it("rejects classic mock functions whose parameters do not match the selected method") {
    given String = "context"
    val value = mock[SignatureShapes]
    "(value.withUsing(_: Int)).expects(1)" shouldNot compile
    "(value.withDefault(_: Int)).expects(1)" shouldNot compile
    "(value.twoArgs(_: Int, 1)).expects(5)" shouldNot compile
    "(value.widened(_: Int)).expects(1)" shouldNot compile
    typeCheckErrors("(value.withUsing(_: Int)).expects(1)").map(_.message).mkString("\n") should
      include("does not match the function's parameter types")
    (value.withUsing(_: Int)(using _: String)).expects(1, "context").returning("full")
    (value.twoArgs(_: Int, _: Int)).expects(1, 2).returning("both")
    value.withUsing(1) shouldBe "full"
    value.twoArgs(1, 2) shouldBe "both"
  }

  it("rejects classic stub functions whose parameters do not match the selected method") {
    given String = "context"
    val value = stub[SignatureShapes]
    "(value.withUsing(_: Int)).when(1)" shouldNot compile
    "(value.withDefault(_: Int)).when(1)" shouldNot compile
    "(value.twoArgs(_: Int, 1)).when(5)" shouldNot compile
    "(value.widened(_: Int)).when(1)" shouldNot compile
    typeCheckErrors("(value.withUsing(_: Int)).when(1)").map(_.message).mkString("\n") should
      include("does not match the function's parameter types")
    (value.twoArgs(_: Int, _: Int)).when(1, 2).returns("both")
    value.twoArgs(1, 2) shouldBe "both"
  }
}

class SelectedOverloadNewStubLookupTest extends AnyFunSpec with Stubs with Matchers {
  import SelectedOverloadFixtures.*

  it("binds new stubs to the selected bounded generic overload with separate call histories") {
    val wideFirst = stub[WideBoundFirst]
    (wideFirst.foo[Wide](_: Wide)).returns(_ => "wide")
    (wideFirst.foo[Narrow](_: Narrow)).returns(_ => "narrow")
    wideFirst.foo[Wide](new Wide) shouldBe "wide"
    (wideFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
    (wideFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
    (wideFirst.foo[Wide](_: Wide)).times shouldBe 1
    (wideFirst.foo[Narrow](_: Narrow)).times shouldBe 2

    val narrowFirst = stub[NarrowBoundFirst]
    (narrowFirst.foo[Wide](_: Wide)).returns(_ => "wide")
    (narrowFirst.foo[Narrow](_: Narrow)).returns(_ => "narrow")
    narrowFirst.foo[Wide](new Wide) shouldBe "wide"
    (narrowFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
    (narrowFirst.foo[Narrow](new Narrow): String) shouldBe "narrow"
    (narrowFirst.foo[Wide](_: Wide)).times shouldBe 1
    (narrowFirst.foo[Narrow](_: Narrow)).times shouldBe 2
  }

  it("binds a new stub to the selected overload for related parameter types") {
    val value = stub[RelatedOverloads]
    (value.choose(_: CharSequence)).returns(_ => "sequence")
    (value.choose(_: String)).returns(_ => "string")
    value.choose("wide": CharSequence) shouldBe "sequence"
    value.choose("narrow") shouldBe "string"
  }

  it("rejects a new stub on a final overload instead of binding its sibling") {
    val value = stub[FinalNarrowOverload]
    "(value.pick(_: String)).returns(_ => \"narrow\")" shouldNot compile
    typeCheckErrors("(value.pick(_: String)).returns(_ => \"narrow\")").map(_.message).mkString("\n") should
      include("is unavailable for mocking: it is final")
    (value.pick(_: CharSequence)).returns(_ => "open stub")
    value.pick("wide": CharSequence) shouldBe "open stub"
  }

  it("binds a new stub through an opaque receiver to the class's override") {
    val derived = stub[OpaqueDerived]
    val value = OpaqueReceivers.wrapStub(derived)
    value.f.returns(number => "override" + number)
    (derived.f(_: String)).returns(text => "sibling" + text)
    value.f(1) shouldBe "override1"
    derived.f("s") shouldBe "siblings"
    value.f.times shouldBe 1
    (derived.f(_: String)).times shouldBe 1
  }

  it("binds a new stub through an opaque receiver to a member from another parent") {
    val derived = stub[DiamondBoth]
    val value = DiamondReceivers.wrapStub(derived)
    value.f.returns(number => "implementation" + number)
    (derived.f(_: String)).returns(text => "sibling" + text)
    (value.h[Int](_: Int)).returns(number => "generic" + number)
    value.f(1) shouldBe "implementation1"
    derived.f("s") shouldBe "siblings"
    value.h[Int](2) shouldBe "generic2"
    value.f.times shouldBe 1
    (derived.f(_: String)).times shouldBe 1
    (value.h[Int](_: Int)).times shouldBe 1
  }

  it("binds a new stub through an opaque receiver to a member from a generic parent") {
    val derived = stub[GenericBoth]
    val value = GenericDiamondReceivers.wrapStub(derived)
    value.g.returns(number => "implementation" + number)
    (derived.g(_: String)).returns(text => "sibling" + text)
    value.g(1) shouldBe "implementation1"
    derived.g("s") shouldBe "siblings"
    value.g.times shouldBe 1
    (derived.g(_: String)).times shouldBe 1
  }

  it("keeps new stub overloads with the same erased parameters apart") {
    val value = stub[ErasedTwins]
    (value.f(_: List[Int])).returns(_.sum)
    (value.f(_: List[String])).returns(_.mkString)
    value.f(List(1, 2)) shouldBe 3
    value.f(List("a", "b")) shouldBe "ab"
    (value.f(_: List[Int])).times shouldBe 1
    (value.f(_: List[String])).times shouldBe 1
  }

  it("binds a new stub through an opaque receiver past an erased-twin sibling") {
    val derived = stub[TwinBoth]
    val value = TwinReceivers.wrapStub(derived)
    (value.t(_: List[Int])).returns(_.sum)
    (derived.t(_: List[String])).returns(_.mkString)
    value.t(List(1, 2)) shouldBe 3
    derived.t(List("a", "b")) shouldBe "ab"
    (value.t(_: List[Int])).times shouldBe 1
    (derived.t(_: List[String])).times shouldBe 1
  }

  it("binds a new stub through an opaque receiver to an aligned polymorphic member") {
    val derived = stub[PolyBoth]
    val value = PolyReceivers.wrapStub(derived)
    (value.h[Int](_: Int)).returns(number => "int" + number)
    value.h[Int](1) shouldBe "int1"
    derived.h[Int](2) shouldBe "int2"
    (value.h[Int](_: Int)).times shouldBe 2
  }

  it("binds new stubs through an opaque receiver to dependent and higher-kinded members") {
    val value = SignatureReceivers.wrapStub(stub[SignatureBoth])
    (value.dep[IntCommand](_: IntCommand)(_: Int)).returns((_, number) => s"dep$number")
    (value.hk[String, [X] =>> Either[String, X]](_: Either[String, Int])).returns(_ => "hk")
    value.dep(new IntCommand)(5) shouldBe "dep5"
    value.hk[String, [X] =>> Either[String, X]](scala.util.Right(5)) shouldBe "hk"
  }

  it("rejects new stub functions whose parameters do not match the selected method") {
    given String = "context"
    val value = stub[SignatureShapes]
    "(value.withUsing(_: Int)).returns(_ => \"x\")" shouldNot compile
    "(value.withDefault(_: Int)).returns(_ => \"x\")" shouldNot compile
    "(value.twoArgs(_: Int, 1)).returns(_ => \"x\")" shouldNot compile
    "(value.widened(_: Int)).returns(_ => \"x\")" shouldNot compile
    "value.fetch(5).returns(_ => Future.successful(1))" shouldNot compile
    typeCheckErrors("value.fetch(5).returns(_ => Future.successful(1))").map(_.message).mkString("\n") should
      include("does not match the function's parameter types")
    (value.twoArgs(_: Int, _: Int)).returns((a, b) => s"$a-$b")
    value.twoArgs(1, 2) shouldBe "1-2"
    (value.twoArgs(_: Int, _: Int)).times shouldBe 1
  }
}
