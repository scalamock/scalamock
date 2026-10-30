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

}
