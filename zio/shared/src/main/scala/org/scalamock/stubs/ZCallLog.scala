package org.scalamock.stubs

import org.scalamock.stubs._
import org.scalamock.stubs.ZStubbedZIOMethod.HasStringId
import org.scalamock.stubs.internal.{CallLogOnFiberRef, StubsOrderAlg}
import zio._


/**
 * This Api is experimental and might be changed in the future:
 * we would like `asString` identificator to be effectless")
 * */
trait ZCallLog extends StubsOrderAlg {

  final def isBefore[R1, R <: R1](first: HasStringId[R1], second: HasStringId[R]): URIO[R, Boolean] =
    (calledMethods <&> first.asString <&> second.asString).map((super.isBefore _).tupled)

  final def isAfter[R1, R <: R1](first: HasStringId[R1], second: HasStringId[R]): URIO[R, Boolean] =
    (calledMethods <&> first.asString <&> second.asString).map((super.isAfter _).tupled)

  protected[scalamock] def calledMethods: UIO[Seq[String]]
}


object ZCallLog extends ZCallLog {
  private lazy val zioApi: CallLog.EffectfulAPI[IO] = CallLogOnFiberRef()

  protected[scalamock] override val calledMethods: UIO[Seq[String]] = zioApi.calledMethods

  def apply(): CallLog = new CallLog {
    override def effectfulAPI(io: StubIO): CallLog.EffectfulAPI[io.F] = io match {
      case _: ZIOStubIO => zioApi.asInstanceOf[CallLog.EffectfulAPI[io.F]]
      case _ => throw new IllegalStateException(
        s"ZCallLog is supposed to be used inside `stubZIO` macros with ZIOStubIO; received io: $io"
      )
    }
  }
}
