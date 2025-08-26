package org.scalamock.stubs

import cats.effect.IO
import scala.language.experimental.macros
import scala.language.implicitConversions
import org.scalamock.stubs.internal.CatsStubMakerImpl

trait CatsEffectStubs extends StubsBase {

  private[scalamock]
  class CatsEffectStubIO extends StubIO {
    type F[+A, +B] = IO[B]

    def die(ex: Throwable): F[Nothing, Nothing] = IO.raiseError(ex)
    def succeed[T](t: => T): F[Nothing, T] = IO(t)
    def flatMap[E, EE >: E, T, T2](fa: IO[T])(f: T => IO[T2]) = fa.flatMap(f)
  }

  final implicit val stubIO: CatsEffectStubIO = new CatsEffectStubIO()

  implicit def stubbed[R](f: => R)(implicit ev: R <:< IO[_]): StubbedIOMethod[Unit, R] =
    macro CatsStubMakerImpl.toStubbedMethod00[R]

  implicit def stubbed[R](f: () => R): StubbedIOMethod[Unit, R] =
    macro CatsStubMakerImpl.toStubbedMethod0[R]

  implicit def stubbed[T1, R](f: T1 => R): StubbedIOMethod[T1, R] =
    macro CatsStubMakerImpl.toStubbedMethod1[T1, R]

  implicit def stubbed[T1, T2, R](f: (T1, T2) => R): StubbedIOMethod[(T1, T2), R] =
    macro CatsStubMakerImpl.toStubbedMethod2[T1, T2, R]

  implicit def stubbed[T1, T2, T3, R](f: (T1, T2, T3) => R): StubbedIOMethod[(T1, T2, T3), R] =
    macro CatsStubMakerImpl.toStubbedMethod3[T1, T2, T3, R]

  implicit def stubbed[T1, T2, T3, T4, R](f: (T1, T2, T3, T4) => R): StubbedIOMethod[(T1, T2, T3, T4), R] =
    macro CatsStubMakerImpl.toStubbedMethod4[T1, T2, T3, T4, R]

  implicit def stubbed[T1, T2, T3, T4, T5, R](f: (T1, T2, T3, T4, T5) => R): StubbedIOMethod[(T1, T2, T3, T4, T5), R] =
    macro CatsStubMakerImpl.toStubbedMethod5[T1, T2, T3, T4, T5, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, R](f: (T1, T2, T3, T4, T5, T6) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6), R] =
    macro CatsStubMakerImpl.toStubbedMethod6[T1, T2, T3, T4, T5, T6, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, R](f: (T1, T2, T3, T4, T5, T6, T7) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    macro CatsStubMakerImpl.toStubbedMethod7[T1, T2, T3, T4, T5, T6, T7, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    macro CatsStubMakerImpl.toStubbedMethod8[T1, T2, T3, T4, T5, T6, T7, T8, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    macro CatsStubMakerImpl.toStubbedMethod9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    macro CatsStubMakerImpl.toStubbedMethod10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    macro CatsStubMakerImpl.toStubbedMethod11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    macro CatsStubMakerImpl.toStubbedMethod12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    macro CatsStubMakerImpl.toStubbedMethod13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    macro CatsStubMakerImpl.toStubbedMethod14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    macro CatsStubMakerImpl.toStubbedMethod15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    macro CatsStubMakerImpl.toStubbedMethod16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    macro CatsStubMakerImpl.toStubbedMethod17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    macro CatsStubMakerImpl.toStubbedMethod18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    macro CatsStubMakerImpl.toStubbedMethod19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    macro CatsStubMakerImpl.toStubbedMethod20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    macro CatsStubMakerImpl.toStubbedMethod21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    macro CatsStubMakerImpl.toStubbedMethod22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R]

}
