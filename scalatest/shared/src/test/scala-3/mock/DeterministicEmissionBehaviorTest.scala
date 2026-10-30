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
import org.scalamock.stubs.{CallLog, Stubs}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future
import scala.util.Success

object DeterministicEmissionFixtures {
  class CollidingSignatureAa
  class CollidingSignatureBB

  trait Inherited {
    val inheritedReference: String
    def inherited(value: Long): String
  }

  trait Service extends Inherited {
    val deferredReference: String
    def zeta(): String
    def alpha(value: Int): String
    def alpha(value: String): String
    def collide(value: CollidingSignatureAa): String
    def collide(value: CollidingSignatureBB): String
  }

  trait MixedValAndMethods {
    val x: String
    def x(value: Int): String
    def x(value: String): String
  }

  trait MethodBase {
    def x(value: Int): String
  }

  trait MethodWithSameNameVal extends MethodBase {
    val x: String
  }

  // Future-typed so that new stubs can configure the vals too.
  trait FutureValBase {
    val x: Future[Int]
  }

  trait MethodBesideInheritedVal extends FutureValBase {
    def x(value: Int): Future[Int]
  }

  trait FutureMethodBase {
    def x(value: Int): Future[Int]
  }

  trait ValBesideInheritedMethod extends FutureMethodBase {
    val x: Future[Int]
  }

  trait DefBase {
    def member: Future[Int]
  }

  // The val overrides DefBase.member, so lookup through DefBase must still find the val's field.
  trait ValOverridingDef extends DefBase {
    override val member: Future[Int]
    def member(value: Int): Future[Int]
  }

  trait ValParent {
    val member: Future[Int]
  }

  trait DefParent {
    def member: Future[Int]
  }

  trait MethodParent {
    def member(value: Int): Future[Int]
  }

  // The inherited val implements DefParent.member without overriding it.
  trait InheritedValAndDef extends DefParent with ValParent

  trait OverridingValAndDef extends DefParent with ValParent {
    override val member: Future[Int]
  }

  // Through MethodParent only member(Int) is visible; the val must not take its field.
  trait ValAndMethodParent extends MethodParent with DefParent {
    override val member: Future[Int]
  }

  trait GeneratedInitializers {
    // Keep zeta before alpha: generated fields must initialize in name order.
    def zeta(): String
    def alpha(): String
  }

}

class DeterministicEmissionBehaviorTest extends AnyFunSpec with MockFactory with Matchers {
  import DeterministicEmissionFixtures.*

  it("preserves named mock expectations for inherited methods and colliding overloads") {
    val value = mock[Service]("ordered")
    (value.alpha(_: Int)).expects(17).returning("int")
    (value.alpha(_: String)).expects("s").returning("string")
    (value.collide(_: CollidingSignatureAa)).expects(*).returning("aa")
    (value.collide(_: CollidingSignatureBB)).expects(*).returning("bb")
    (value.inherited(_: Long)).expects(9L).returning("inherited")
    value.alpha(17) shouldBe "int"
    value.alpha("s") shouldBe "string"
    value.collide(new CollidingSignatureAa) shouldBe "aa"
    value.collide(new CollidingSignatureBB) shouldBe "bb"
    value.inherited(9L) shouldBe "inherited"
    value.deferredReference shouldBe null
    value.inheritedReference shouldBe null
  }

  it("preserves classic stub defaults and independent overload responses") {
    val value = stub[Service]
    (value.alpha(_: Int)).when(17).returns("int")
    (value.alpha(_: String)).when("s").returns("string")
    (value.inherited(_: Long)).when(9L).returns("inherited")
    value.zeta() shouldBe null
    value.alpha(17) shouldBe "int"
    value.alpha("s") shouldBe "string"
    value.inherited(9L) shouldBe "inherited"
    value.deferredReference shouldBe null
    value.inheritedReference shouldBe null
  }

  it("separates a deferred val from classic mock methods with the same name") {
    val value = mock[MixedValAndMethods]
    (value.x(_: Int)).expects(7).returning("int")
    (value.x(_: String)).expects("s").returning("string")
    value.x shouldBe null
    value.x(7) shouldBe "int"
    value.x("s") shouldBe "string"
  }

  it("separates a deferred val from classic stub methods with the same name") {
    val value = stub[MixedValAndMethods]
    (value.x(_: Int)).when(7).returns("int")
    (value.x(_: String)).when("s").returns("string")
    value.x shouldBe null
    value.x(7) shouldBe "int"
    value.x("s") shouldBe "string"
    value.x(8) shouldBe null
  }

  it("keeps inherited classic mock lookup through a supertype when a val shares the name") {
    val derived = mock[MethodWithSameNameVal]
    val value: MethodBase = derived
    (value.x(_: Int)).expects(7).returning("base")
    value.x(7) shouldBe "base"
    derived.x shouldBe null
  }

  it("keeps inherited classic stub lookup through a supertype when a val shares the name") {
    val derived = stub[MethodWithSameNameVal]
    val value: MethodBase = derived
    (value.x(_: Int)).when(7).returns("base")
    value.x(7) shouldBe "base"
    derived.x shouldBe null
  }

  it("keeps a classic mock method added beside an inherited val") {
    val derived = mock[MethodBesideInheritedVal]
    val base: FutureValBase = derived
    (derived.x(_: Int)).expects(1).returning(Future.successful(2))
    base.x shouldBe null
    derived.x(1).value shouldBe Some(Success(2))
  }

  it("keeps a classic stub method added beside an inherited val") {
    val derived = stub[MethodBesideInheritedVal]
    val base: FutureValBase = derived
    (derived.x(_: Int)).when(1).returns(Future.successful(2))
    base.x shouldBe null
    derived.x(1).value shouldBe Some(Success(2))
  }

  it("configures a classic mock method through base and derived references when a val is added") {
    val derived = mock[ValBesideInheritedMethod]
    val base: FutureMethodBase = derived
    (base.x(_: Int)).expects(1).returning(Future.successful(2))
    (derived.x(_: Int)).expects(3).returning(Future.successful(4))
    derived.x shouldBe null
    base.x(1).value shouldBe Some(Success(2))
    derived.x(3).value shouldBe Some(Success(4))
  }

  it("configures a classic stub val that overrides a parameterless def through either reference") {
    val derived = stub[ValOverridingDef]
    val base: DefBase = derived
    (() => derived.member).when().returns(Future.successful(1))
    (() => base.member).when().returns(Future.successful(2))
    (derived.member(_: Int)).when(3).returns(Future.successful(4))
    derived.member shouldBe null
    derived.member(3).value shouldBe Some(Success(4))
  }

  it("configures a classic stub val through both its val and def parents") {
    val inherited = stub[InheritedValAndDef]
    (() => (inherited: ValParent).member).when().returns(Future.successful(1))
    (() => (inherited: DefParent).member).when().returns(Future.successful(2))
    inherited.member shouldBe null

    val overriding = stub[OverridingValAndDef]
    (() => (overriding: ValParent).member).when().returns(Future.successful(1))
    (() => (overriding: DefParent).member).when().returns(Future.successful(2))
    overriding.member shouldBe null
  }

  it("keeps a classic stub method from a method-only parent apart from a same-name val") {
    val derived = stub[ValAndMethodParent]
    val viaMethod: MethodParent = derived
    val viaDef: DefParent = derived
    (viaMethod.member(_: Int)).when(1).returns(Future.successful(2))
    (() => viaDef.member).when().returns(Future.successful(42))
    derived.member(1).value shouldBe Some(Success(2))
    derived.member shouldBe null
  }

  it("keeps a classic mock method from a method-only parent apart from a same-name val") {
    val derived = mock[ValAndMethodParent]
    val viaMethod: MethodParent = derived
    (viaMethod.member(_: Int)).expects(1).returning(Future.successful(2))
    derived.member(1).value shouldBe Some(Success(2))
  }

  it("configures a classic stub method through base and derived references when a val is added") {
    val derived = stub[ValBesideInheritedMethod]
    val base: FutureMethodBase = derived
    (base.x(_: Int)).when(1).returns(Future.successful(2))
    (derived.x(_: Int)).when(3).returns(Future.successful(4))
    derived.x shouldBe null
    base.x(1).value shouldBe Some(Success(2))
    derived.x(3).value shouldBe Some(Success(4))
  }

  it("initializes generated stub defaults once in deterministic member order") {
    var initialized = 0
    // Generated mock functions capture this call-site Defaultable[Any] during initialization.
    implicit def generatedDefault: org.scalamock.util.Defaultable[Any] = {
      initialized += 1
      val result = "default-" + initialized
      new org.scalamock.util.Defaultable[Any] { val default: Any = result }
    }
    val value = stub[GeneratedInitializers]
    initialized shouldBe 2
    value.alpha() shouldBe "default-1"
    value.zeta() shouldBe "default-2"
    initialized shouldBe 2
  }

}

class DeterministicNewStubBehaviorTest extends AnyFunSpec with Stubs with Matchers {
  import DeterministicEmissionFixtures.*

  it("preserves new stub overloads, call order and reset behavior") {
    implicit val log: CallLog = CallLog()
    val value = stub[Service]
    (value.alpha(_: Int)).returns(number => "int" + number)
    (value.alpha(_: String)).returns(text => "string" + text)
    (value.collide(_: CollidingSignatureAa)).returns(_ => "aa")
    (value.collide(_: CollidingSignatureBB)).returns(_ => "bb")
    (value.inherited(_: Long)).returns(number => "inherited" + number)
    value.alpha(17) shouldBe "int17"
    value.alpha("s") shouldBe "strings"
    value.collide(new CollidingSignatureAa) shouldBe "aa"
    value.collide(new CollidingSignatureBB) shouldBe "bb"
    value.inherited(9L) shouldBe "inherited9"
    (value.alpha(_: Int)).isBefore(value.inherited) shouldBe true
    (value.alpha(_: String)).times shouldBe 1
    value.deferredReference shouldBe null
    value.inheritedReference shouldBe null
    resetStubs()
    (value.alpha(_: Int)).times shouldBe 0
    (value.alpha(_: String)).times shouldBe 0
    value.inherited.times shouldBe 0
  }

  it("separates a deferred val from new stub methods with the same name") {
    val value = stub[MixedValAndMethods]
    (value.x(_: Int)).returns(number => "int" + number)
    (value.x(_: String)).returns(text => "string" + text)
    value.x shouldBe null
    value.x(7) shouldBe "int7"
    value.x("s") shouldBe "strings"
    (value.x(_: Int)).times shouldBe 1
    (value.x(_: String)).times shouldBe 1
    resetStubs()
    (value.x(_: Int)).times shouldBe 0
    (value.x(_: String)).times shouldBe 0
  }

  it("keeps inherited new stub lookup through a supertype when a val shares the name") {
    val derived = stub[MethodWithSameNameVal]
    val value: org.scalamock.stubs.Stub[MethodBase] = derived
    (value.x(_: Int)).returns(number => "base" + number)
    value.x(7) shouldBe "base7"
    (value.x(_: Int)).times shouldBe 1
    derived.x shouldBe null
  }

  it("keeps a new stub method added beside an inherited val when the val is configured") {
    val derived = stub[MethodBesideInheritedVal]
    val base: org.scalamock.stubs.Stub[FutureValBase] = derived
    (derived.x(_: Int)).returns(value => Future.successful(value + 1))
    base.x.returns(_ => Future.successful(42))
    derived.x.returns(_ => Future.successful(43))
    derived.x(1).value shouldBe Some(Success(2))
    (derived.x(_: Int)).times shouldBe 1
    derived.x shouldBe null
  }

  it("configures a new stub val that overrides a parameterless def through either reference") {
    val derived = stub[ValOverridingDef]
    val base: org.scalamock.stubs.Stub[DefBase] = derived
    (derived.member(_: Int)).returns(value => Future.successful(value + 1))
    derived.member.returns(_ => Future.successful(42))
    base.member.returns(_ => Future.successful(43))
    derived.member(1).value shouldBe Some(Success(2))
    (derived.member(_: Int)).times shouldBe 1
    derived.member shouldBe null
  }

  it("configures a new stub val through both its val and def parents") {
    val inherited = stub[InheritedValAndDef]
    val inheritedViaVal: org.scalamock.stubs.Stub[ValParent] = inherited
    val inheritedViaDef: org.scalamock.stubs.Stub[DefParent] = inherited
    inheritedViaVal.member.returns(_ => Future.successful(1))
    inheritedViaDef.member.returns(_ => Future.successful(2))
    inherited.member.returns(_ => Future.successful(3))
    inherited.member shouldBe null

    val overriding = stub[OverridingValAndDef]
    val overridingViaVal: org.scalamock.stubs.Stub[ValParent] = overriding
    val overridingViaDef: org.scalamock.stubs.Stub[DefParent] = overriding
    overridingViaVal.member.returns(_ => Future.successful(1))
    overridingViaDef.member.returns(_ => Future.successful(2))
    overriding.member.returns(_ => Future.successful(3))
    overriding.member shouldBe null
  }

  it("keeps a new stub method from a method-only parent apart from a same-name val") {
    val derived = stub[ValAndMethodParent]
    val viaMethod: org.scalamock.stubs.Stub[MethodParent] = derived
    val viaDef: org.scalamock.stubs.Stub[DefParent] = derived
    (viaMethod.member(_: Int)).returns(value => Future.successful(value + 1))
    viaDef.member.returns(_ => Future.successful(42))
    derived.member(1).value shouldBe Some(Success(2))
    (viaMethod.member(_: Int)).times shouldBe 1
    derived.member shouldBe null
  }

  it("keeps an inherited new stub method when a val added beside it is configured") {
    val derived = stub[ValBesideInheritedMethod]
    val base: org.scalamock.stubs.Stub[FutureMethodBase] = derived
    (base.x(_: Int)).returns(value => Future.successful(value + 1))
    derived.x.returns(_ => Future.successful(42))
    base.x(1).value shouldBe Some(Success(2))
    derived.x(2).value shouldBe Some(Success(3))
    (base.x(_: Int)).times shouldBe 2
    (derived.x(_: Int)).times shouldBe 2
    derived.x shouldBe null
  }
}
