package org.scalamock.stubs

import zio._

/**
 * Representation of stubbed method without arguments.
 *
 * [[ZIOStubs]] interface provides implicit conversion from selected method to StubbedMethodZIO0.
 * {{{
 *   trait Foo:
 *     def foo00(): String
 *     def fooIO: IO[String, Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * The default way of getting stub for such method - convert it to a function () => R.
 *
 * Exclusively for ZIO - you can omit converting it to function
 *
 * {{{
 *   val foo00Stubbed: StubbedMethod0[String] = () => foo.foo00()
 *   val fooIOStubbed: StubbedMethod0[IO[String, Int]] = foo.fooIO
 * }}}
 * */
class StubbedZIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  /** Allows to set result for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).returnsZIO("result")
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *    } yield ()
   * }}}
   * */
  @deprecated(
    "Use `returnsZIOWith`, `succeedsWith`, `failsWith`, `diesWith` instead. Will be deleted in first release after 01.07.2025. This is needed to replace StubbedMethod0[R] with StubbedMethod[Unit, R]"
  )
  def returnsZIO(f: => R): UIO[Unit] = ZIO.succeed(returns(f))

  /** Allows to set result for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).returnsZIOWith("result")
   *      _ <- foo.fooIO.returnsZIOWith(ZIO.succeed(1))
   *    } yield ()
   * }}}
   * */
  def returnsZIOWith(value: => R): UIO[Unit] = returnsZIO(value)

  /** Allows to set success for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).succeedsWith("result")
   *      _ <- foo.fooIO.succeedsWith(1)
   *    } yield ()
   * }}}
   * */
  def succeedsWith[RR](f: => RR)(implicit ev: IO[Nothing, RR] <:< R): UIO[Unit] =
    returnsZIOWith(ev(ZIO.succeed(f)))

  /** Allows set fail result for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).failsWith("result")
   *      _ <- foo.fooIO.failsWith(1)
   *    } yield ()
   * }}}
   * */
  def failsWith[RR](f: => RR)(implicit ev: IO[RR, Nothing] <:< R): UIO[Unit] =
    returnsZIOWith(ev(ZIO.fail(f)))

  /** Allows set die result for method without arguments. Returns ZIO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).diesWith(new Exception("foo"))
   *      _ <- foo.fooIO.diesWith(new Exception("bar"))
   *    } yield ()
   * }}}
   * */
  def diesWith(f: => Throwable)(implicit ev: UIO[Nothing] <:< R): UIO[Unit] =
    returnsZIOWith(ev(Exit.die(f)))

  /** Allows to get number of times method was executed. Returns ZIO
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *      fooIOTimes <- foo.fooIO.timesZIO
   *    } yield fooIOTimes == 11 // true
   * }}}
   * */
  def timesZIO: UIO[Int] = ZIO.succeed(times)

  /** Allows to set result for method without arguments.
   *
   * {{{
   *   (() => foo.foo00()).returns("result")
   *   foo.fooIO.returns(ZIO.succeed(1))
   * }}}
   * */
  @deprecated(
    "Use `returnsWith` instead. Will be deleted in first release after 01.07.2025. This is needed to replace StubbedMethod0[R] with StubbedMethod[Unit, R]"
  )
  def returns(f: => R): Unit = delegate.returns(f)

  /** Allows to set result for method without arguments.
   *
   * {{{
   *   (() => foo.foo00()).returnsWith("result")
   *   foo.fooIO.returnsWith(ZIO.succeed(1))
   * }}}
   * */
  def returnsWith(value: => R) = delegate.returnsWith(value)

  /** Allows to get number of times method was executed.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *    } yield foo.fooIO.times == 11 // true
   * }}}
   * */
  def times: Int = delegate.times

  /**
   * Returns true if this method was called before other method.
   */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  /**
   * Returns true if this method was called after other method.
   */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /**
   *  Returns string representation of method.
   *  Representation currently depends on scala version.
   * */
  def asString: String = delegate.asString

  override def toString: String = asString
}

/**
 * Representation of stubbed method with arguments.
 *
 * [[ZIOStubs]] interface provides implicit conversion from selected method to StubbedMethodZIO.
 * {{{
 *   trait Foo:
 *     def foo(x: Int): UIO[String]
 *     def bar(x: Int, y: String): IO[String, Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * Scala 3
 * {{{
 *   val fooStubbed: StubbedMethod[Int, UIO[String]] = foo.foo
 *   val barStubbed: StubbedMethod[(Int, String), IO[String, Int]] = foo.bar
 * }}}
 *
 * Scala 2
 * {{{
 *   val fooStubbed: StubbedMethod[Int, UIO[String]] = foo.foo _
 *   val barStubbed: StubbedMethod[(Int, String), IO[String, Int]] = foo.bar _
 * }}}
 * */
class StubbedZIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] with ZStubbedZIOMethod[Any, A, R] {
  
  override protected val delegateZIO: URIO[Any, StubbedMethod[A, R]] = ZIO.succeed(delegate)

  /** Allows to set result for method with arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.bar.returns((x, y) => ZIO.succeed(1))
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   (foo.bar _).returns((x, y) => ZIO.succeed(1))
   *  }}}
   * */
  def returns(f: A => R): Unit = delegate.returns(f)

  /** Allows to set result for method without arguments.
   *
   * Scala 3
   * {{{
   *   foo.bar.returnsWith(ZIO.succeed(1))
   * }}}
   * Scala 2
   * {{{
   *   (foo.bar _).returnsWith(ZIO.succeed(1))
   * }}}
   * */
  def returnsWith(value: => R) = delegate.returnsWith(value)

  /** Allows to get number of times method was executed.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsZIO(ZIO.succeed(1))
   *      _ <- foo.fooIO.repeatN(10)
   *    } yield foo.fooIO.times == 11 // true
   * }}}
   * */
  def times: Int = delegate.times

  /** Allows to get arguments with which method was executed.
   *  Returns multiple arguments as tuple.
   *  One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for {
   *     _ <- foo.bar.returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *   } yield foo.bar.calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *   } yield (foo.bar _).calls == List((1, "foo"), (2, "bar")) // true
   *   }}}
   * */
  def calls: List[A] = delegate.calls

  /**
   * Returns true if this method was called before other method.
   */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  /**
   * Returns true if this method was called after other method.
   */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /**
   *  Returns string representation of method.
   *  Representation currently depends on scala version.
   * */
  def asString: String = delegate.asString

  override def toString: String = asString
}
