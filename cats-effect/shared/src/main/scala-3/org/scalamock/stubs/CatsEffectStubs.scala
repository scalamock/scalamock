package org.scalamock.stubs

import cats.effect.IO
import org.scalamock.stubs.StubbedIOMethod
import scala.language.implicitConversions
import scala.util.NotGiven
import scala.concurrent.Future

trait CatsEffectStubs extends StubsBase {

  private[scalamock]
  class CatsEffectStubIO extends StubIO {
    type F[+A, +B] = IO[B]

    def die(ex: Throwable): F[Nothing, Nothing] = IO.raiseError(ex)
    def succeed[T](t: => T): F[Nothing, T] = IO(t)
    def flatMap[E, EE >: E, T, T2](fa: IO[T])(f: T => IO[T2]) = fa.flatMap(f)
  }

  final given CatsEffectStubIO = CatsEffectStubIO()

  implicit inline def stubbed[R](inline f: => R)(using R <:< IO[?]): StubbedIOMethod[Unit, R] =
    StubbedIOMethod(stubbed00Impl[R](f))

  implicit inline def stubbed[R](inline f: () => R)(using R =:= R): StubbedIOMethod[Unit, R] =
    StubbedIOMethod(stubbed0Impl[R](f))

  implicit inline def stubbed[T1, R](inline f: T1 => R)(using (T1, R) =:= (T1, R)): StubbedIOMethod[T1, R] =
    StubbedIOMethod(stubbed1Impl[T1, R](f))

  implicit inline def stubbed[T1, T2, R](inline f: (T1, T2) => R)(using (T1, T2, R) =:= (T1, T2, R)): StubbedIOMethod[(T1, T2), R] =
    StubbedIOMethod(stubbed2Impl[T1, T2, R](f))

  implicit inline def stubbed[T1, T2, T3, R](inline f: (T1, T2, T3) => R)(using (T1, T2, T3, R) =:= (T1, T2, T3, R)): StubbedIOMethod[(T1, T2, T3), R] =
    StubbedIOMethod(stubbed3Impl[T1, T2, T3, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, R](inline f: (T1, T2, T3, T4) => R)(using (T1, T2, T3, T4, R) =:= (T1, T2, T3, T4, R)): StubbedIOMethod[(T1, T2, T3, T4), R] =
    StubbedIOMethod(stubbed4Impl[T1, T2, T3, T4, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, R](inline f: (T1, T2, T3, T4, T5) => R)(using (T1, T2, T3, T4, T5, R) =:= (T1, T2, T3, T4, T5, R)): StubbedIOMethod[(T1, T2, T3, T4, T5), R] =
    StubbedIOMethod(stubbed5Impl[T1, T2, T3, T4, T5, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, R](inline f: (T1, T2, T3, T4, T5, T6) => R)(using (T1, T2, T3, T4, T5, T6, R) =:= (T1, T2, T3, T4, T5, T6, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6), R] =
    StubbedIOMethod(stubbed6Impl[T1, T2, T3, T4, T5, T6, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, R](inline f: (T1, T2, T3, T4, T5, T6, T7) => R)(using (T1, T2, T3, T4, T5, T6, T7, R) =:= (T1, T2, T3, T4, T5, T6, T7, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    StubbedIOMethod(stubbed7Impl[T1, T2, T3, T4, T5, T6, T7, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    StubbedIOMethod(stubbed8Impl[T1, T2, T3, T4, T5, T6, T7, T8, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    StubbedIOMethod(stubbed9Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    StubbedIOMethod(stubbed10Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    StubbedIOMethod(stubbed11Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    StubbedIOMethod(stubbed12Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    StubbedIOMethod(stubbed13Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    StubbedIOMethod(stubbed14Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    StubbedIOMethod(stubbed15Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    StubbedIOMethod(stubbed16Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    StubbedIOMethod(stubbed17Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    StubbedIOMethod(stubbed18Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    StubbedIOMethod(stubbed19Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    StubbedIOMethod(stubbed20Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    StubbedIOMethod(stubbed21Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f))

  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R)): StubbedIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    StubbedIOMethod(stubbed22Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f))

}
