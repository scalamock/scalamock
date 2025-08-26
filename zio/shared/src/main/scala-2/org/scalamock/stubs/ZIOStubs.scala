package org.scalamock.stubs

import zio._
import scala.language.experimental.macros
import scala.language.implicitConversions
import org.scalamock.stubs.internal.ZIOStubMakerImpl
import scala.concurrent.Future

trait ZIOStubs extends StubsBase {
  private[scalamock] class ZIOStubIO extends StubIO {
    type F[+A, +B] = IO[A, B]

    def die(ex: Throwable): F[Nothing, Nothing] = ZIO.die(ex)
    def succeed[T](t: => T): F[Nothing, T] = ZIO.succeed(t)
    def flatMap[E, EE >: E, T, T2](fa: IO[E, T])(f: T => IO[EE, T2]) = fa.flatMap(f)
  }

  final implicit val stubIO: ZIOStubIO = new ZIOStubIO()

  implicit def stubbed[R](f: => R)(implicit ev: R <:< IO[Any, Any]): StubbedZIOMethod[Unit, R] =
    macro ZIOStubMakerImpl.toStubbedMethod00[R]

  implicit def stubbed[R](f: () => R): StubbedZIOMethod[Unit, R] =
    macro ZIOStubMakerImpl.toStubbedMethod0[R]

  implicit def stubbed[T1, R](f: T1 => R): StubbedZIOMethod[T1, R] =
    macro ZIOStubMakerImpl.toStubbedMethod1[T1, R]

  implicit def stubbed[T1, T2, R](f: (T1, T2) => R): StubbedZIOMethod[(T1, T2), R] =
    macro ZIOStubMakerImpl.toStubbedMethod2[T1, T2, R]

  implicit def stubbed[T1, T2, T3, R](f: (T1, T2, T3) => R): StubbedZIOMethod[(T1, T2, T3), R] =
    macro ZIOStubMakerImpl.toStubbedMethod3[T1, T2, T3, R]

  implicit def stubbed[T1, T2, T3, T4, R](f: (T1, T2, T3, T4) => R): StubbedZIOMethod[(T1, T2, T3, T4), R] =
    macro ZIOStubMakerImpl.toStubbedMethod4[T1, T2, T3, T4, R]

  implicit def stubbed[T1, T2, T3, T4, T5, R](f: (T1, T2, T3, T4, T5) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5), R] =
    macro ZIOStubMakerImpl.toStubbedMethod5[T1, T2, T3, T4, T5, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, R](f: (T1, T2, T3, T4, T5, T6) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6), R] =
    macro ZIOStubMakerImpl.toStubbedMethod6[T1, T2, T3, T4, T5, T6, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, R](f: (T1, T2, T3, T4, T5, T6, T7) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    macro ZIOStubMakerImpl.toStubbedMethod7[T1, T2, T3, T4, T5, T6, T7, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    macro ZIOStubMakerImpl.toStubbedMethod8[T1, T2, T3, T4, T5, T6, T7, T8, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    macro ZIOStubMakerImpl.toStubbedMethod9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    macro ZIOStubMakerImpl.toStubbedMethod10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    macro ZIOStubMakerImpl.toStubbedMethod11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    macro ZIOStubMakerImpl.toStubbedMethod12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    macro ZIOStubMakerImpl.toStubbedMethod13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    macro ZIOStubMakerImpl.toStubbedMethod14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    macro ZIOStubMakerImpl.toStubbedMethod15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    macro ZIOStubMakerImpl.toStubbedMethod16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    macro ZIOStubMakerImpl.toStubbedMethod17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    macro ZIOStubMakerImpl.toStubbedMethod18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    macro ZIOStubMakerImpl.toStubbedMethod19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    macro ZIOStubMakerImpl.toStubbedMethod20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    macro ZIOStubMakerImpl.toStubbedMethod21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R): StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    macro ZIOStubMakerImpl.toStubbedMethod22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R]

}
