package newapi

import org.scalamock.stubs.{CallLog, Stubs, StubbedMethod}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StubbedHigherKindedMethods extends AnyFlatSpec with Matchers with Stubs:
  trait TestTrait:
    def higherKind[F[_], A](fa: F[A]): Unit

  "StubbedMethod for higher-kind methods" should "set the return value for a higher-kind method" in:
    val hktStub = stub[TestTrait]
    hktStub.higherKind[List, Int].returns(_ => ())
    hktStub.higherKind(List(1, 2, 3))
    hktStub.higherKind[List, Int].times shouldBe 1