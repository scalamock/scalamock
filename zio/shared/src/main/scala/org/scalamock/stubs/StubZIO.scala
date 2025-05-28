package org.scalamock.stubs

import org.scalamock.stubs
import zio._

import scala.language.implicitConversions


/**
 * Ideally, we would like to generate direct methods:
 * {{{
 * val stub = mock[Service]
 *
 * for {
 *   _ <- stub.fooBar.returnsZIO(...)
 * } yield ...
 * }}}
 *
 *
 * However, we were not able to write the corresponding macros, so the [[.stubbed]] method was introduced. 
 * This method might be removed in the future:
 *
 * {{{
 * val stub = mock[Service]
 *
 * for {
 *   _ <- stub.stubbed(_.fooBar _).returnsZIO(...)
 * } yield ...
 * }}}
 */
class StubZIO[Service](stub: => Stub[Service])(implicit tag: Tag[Stub[Service]]) {
  /**
   * Support of StubbedMethod0 will become later with another MR or with removing StubbedMethod0
   */
  def stubbed[Args, Zio](f: Stub[Service] => StubbedMethod[Args, Zio]): StubZIOStubbedZIOMethod[Stub[Service], Args, Zio] =
    new StubZIOStubbedZIOMethod(ZIO.serviceWith[Stub[Service]](f))

  // to prevent potential conflicts with generated methods in the future   
  private val _layer: ULayer[Stub[Service]] = ZLayer.succeed(stub)
}

object StubZIO {
  implicit def toLayer[S](stubZio: StubZIO[S]): ULayer[Stub[S]] = stubZio._layer
}

final class StubZIOStubbedZIOMethod[R, Args, Zio] private[scalamock](
  override protected val delegateZIO: URIO[R, StubbedMethod[Args, Zio]]
) extends ZStubbedZIOMethod[R, Args, Zio] with ZStubbedZIOMethod.HasStringId[R] with ZStubbedZIOMethod.Order[R]

