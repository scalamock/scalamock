package org.scalamock.stubs
import zio.{ZIO, IO}

private[scalamock] class ZIOStubIO extends StubIO {
  type F[+A, +B] = IO[A, B]

  def die(ex: Throwable): F[Nothing, Nothing] = ZIO.die(ex)
  def succeed[T](t: => T): F[Nothing, T] = ZIO.succeed(t)
  def flatMap[E, EE >: E, T, T2](fa: IO[E, T])(f: T => IO[EE, T2]) = fa.flatMap(f)
}
