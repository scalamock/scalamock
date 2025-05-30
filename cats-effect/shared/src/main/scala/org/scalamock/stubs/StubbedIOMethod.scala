package org.scalamock.stubs

import cats.effect.IO

/**
 * Representation of stubbed method without arguments.
 *
 * [[CatsEffectStubs]] interface provides implicit conversion from selected method to StubbedMethodIO0.
 * {{{
 *   trait Foo:
 *     def foo00(): String
 *     def fooIO: IO[Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * The default way of getting stub for such method - convert it to a function () => R.
 * 
 * Exclusively for IO - you can omit converting it to function
 *
 * {{{
 *   val foo00Stubbed: StubbedMethod0[String] = () => foo.foo00()
 *   val fooIOStubbed: StubbedMethod0[IO[Int]] = foo.fooIO
 * }}}
 * */
class StubbedIOMethod0[R](delegate: StubbedMethod0[R]) extends StubbedMethod0[R] {
  /** Allows to set result for method without arguments. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).returnsIO("result")
   *      _ <- foo.fooIO.returnsIO(IO(1))
   *    } yield ()
   * }}}
   * */
  @deprecated(
    "Use `returnsIOWith`, `succeedsWith`, `raisesErrorWith` instead. Will be deleted in first release after 01.07.2025. This is needed to replace StubbedMethod0[R] with StubbedMethod[Unit, R]"
  )
  def returnsIO(f: => R): IO[Unit] = IO(returns(f))

  /** Allows to set result for method without arguments. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- (() => foo.foo00()).returnsIOWith("result")
   *      _ <- foo.fooIO.returnsIOWith(IO(1))
   *    } yield ()
   * }}}
   * */
  def returnsIOWith(value: => R): IO[Unit] = returnsIO(value)

  /** Allows to set result for method without arguments.
   *
   * {{{
   *    (() => foo.foo00()).returns("result")
   *    foo.fooIO.returns(IO(1))
   * }}}
   * */
  def returns(f: => R): Unit = delegate.returns(f)

  /** Allows to set result for method without arguments.
   *
   * {{{
   *    (() => foo.foo00()).returnsWith("result")
   *    foo.fooIO.returnsWith(IO(1))
   * }}}
   * */
  def returnsWith(f: => R): Unit = delegate.returnsWith(f)

  /** Allows to set success for method without arguments. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.succeedsWith(1)
   *    } yield ()
   * }}}
   * */
  def succeedsWith[RR](value: => RR)(implicit ev: IO[RR] <:< R): IO[Unit] =
    returnsIOWith(ev(IO(value)))

  /** Allows raise error for method without arguments. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.failsWith(1)
   *    } yield ()
   * }}}
   * */
  def raisesErrorWith(ex: => Throwable)(implicit ev: IO[Nothing] <:< R): IO[Unit] =
    returnsIOWith(ev(IO.raiseError(ex)))

  /** Allows to get number of times method was executed.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsIO(IO(1))
   *      _ <- foo.fooIO
   *      _ <- foo.fooIO
   *    } yield foo.fooIO.times == 2 // true
   * }}}
   * */
  def times: Int = delegate.times

  /** Allows to get number of times method was executed. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.returnsIO(IO(1))
   *      _ <- foo.fooIO
   *      _ <- foo.fooIO
   *      fooIOTimes <- foo.fooIO.timesIO
   *    } yield fooIOTimes == 2 // true
   * }}}
   * */
  def timesIO: IO[Int] = IO(times)


  /** Returns true if this method was called before other method. */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)
    
  /** Returns true if this method was called after other method. */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /** Returns string representation of method.
   *  Representation currently depends on scala version.
   */
  def asString: String = delegate.asString

  override def toString: String = asString
}

/**
 * Representation of stubbed method with arguments.
 *
 * [[CatsEffectStubs]] interface provides implicit conversion from selected method to StubbedMethodIO.
 * {{{
 *   trait Foo:
 *     def foo(x: Int): IO[String]
 *     def bar(x: Int, y: String): IO[Int]
 *
 *   val foo = stub[Foo]
 * }}}
 *
 * Scala 3
 * {{{
 *   val fooStubbed: StubbedMethod[Int, IO[String]] = foo.foo
 *   val barStubbed: StubbedMethod[(Int, String), IO[Int]] = foo.bar
 * }}}
 *
 * Scala 2
 * {{{
 *   val fooStubbed: StubbedMethod[Int, IO[String]] = foo.foo _
 *   val barStubbed: StubbedMethod[(Int, String), IO[Int]] = foo.bar _
 * }}}
 * */
class StubbedIOMethod[A, R](delegate: StubbedMethod[A, R]) extends StubbedMethod[A, R] {
  /** Allows to set result for method with arguments. Returns IO
   * 
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.returnsIO((x, y) => IO(1))
   *   yield ()
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsIO((x, y) => IO(1))
   *   } yield ()
   *  }}}
   * */
  def returnsIO(f: A => R): IO[Unit] = IO(returns(f))

  /** Allows to set result for method with arguments. Returns IO
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.returnsIOWith(IO(1))
   *   yield ()
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsIOWith(IO(1))
   *   } yield ()
   *  }}}
   * */
  def returnsIOWith(value: => R): IO[Unit] = returnsIO(_ => value)

  /** Allows to set success for method with arguments. Returns IO.
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.succeedsWith(1)
   *   yield ()
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).succeedsWith(1)
   *   } yield ()
   *  }}}
   * */
  def succeedsWith[RR](value: => RR)(implicit ev: IO[RR] <:< R): IO[Unit] =
    returnsIO(_ => ev(IO(value)))

  /** Allows raise error for method without arguments. Returns IO.
   *
   * {{{
   *    for {
   *      _ <- foo.fooIO.failsWith(1)
   *    } yield ()
   * }}}
   * */
  def raisesErrorWith(ex: => Throwable)(implicit ev: IO[Nothing] <:< R): IO[Unit] =
    returnsIO(_ => ev(IO.raiseError(ex)))


  /** Allows to get arguments with which method was executed. Returns IO
   * 
   *  Returns multiple arguments as tuple. One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for {
   *     _ <- foo.bar.returnsIO(_ => IO(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- foo.bar.callsIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsIO(_ => IO(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- (foo.bar _).callsIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   * */
  def callsIO: IO[List[A]] = IO(calls)

  /** Allows to get number of times method was executed with specific arguments. Returns IO
   *
   *  Scala 3
   *  {{{
   *    for
   *      _ <- foo.bar.returnsIO(_ => IO(1))
   *      _ <- foo.bar(1, "foo")
   *      _ <- foo.bar(2, "bar")
   *      barTimes <- foo.bar.timesIO
   *    yield barTimes == 2 // true
   * }}}
   * Scala 2
   * {{{
   *    for {
   *      _ <- (foo.bar _).returnsIO(_ => IO(1))
   *      _ <- foo.bar(1, "foo")
   *      _ <- foo.bar(2, "bar")
   *      barTimes <- (foo.bar _).timesIO
   *    } yield barTimes == 2 // true
   * }}}
   * */
  def timesIO: IO[Int] = IO(times)

  /** Allows to get number of times method was executed with specific arguments. Returns IO
   *
   *  Scala 3
   *  {{{
   *    for
   *      _ <- foo.bar.returnsIO(_ => IO(1))
   *      _ <- foo.bar(1, "foo")
   *      _ <- foo.bar(2, "bar")
   *      barTimes <- foo.bar.timesIO((1, "foo"))
   *    yield barTimes == 1 // true
   * }}}
   *  Scala 2
   *  {{{
   *    for {
   *      _ <- (foo.bar _).returnsIO(_ => IO(1))
   *      _ <- foo.bar(1, "foo")
   *      _ <- foo.bar(2, "bar")
   *      barTimes <- (foo.bar _).timesIO((1, "foo"))
   *    } yield barTimes == 1 // true
   * }}}
   * */
  def timesIO(args: A): IO[Int] = IO(times(args))

  /** Allows to set result for method with arguments.
   *
   *  Scala 3
   *  {{{
   *   foo.bar.returns((x, y) => IO(1))
   *  }}}
   *  
   *  Scala 2
   *  {{{
   *   (foo.bar _).returns((x, y) => IO(1))
   *  }}}
   * */
  def returns(f: A => R): Unit = delegate.returns(f)


  /** Allows to set result for method with arguments.
   *
   * Scala 3
   * {{{
   *   foo.bar.returnsWith(IO(1))
   * }}}
   *
   *  Scala 2
   *  {{{
   *   (foo.bar _).returnsWith(IO(1))
   *   }}}
   * */
  def returnsWith(f: => R): Unit = delegate.returnsWith(f)

  def returnsWhen(pf: PartialFunction[A, R]): Unit = delegate.returnsWhen(pf)

  /** Allows to get number of times method was executed.
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.foo.returnsIO(x => IO(5))
   *     _ <- foo.foo(1)
   *     _ <- foo.foo(100)
   *   yield foo.foo.times == 2 // true
   *  }}}
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.foo _).returnsIO(x => IO(5))
   *     _ <- foo.foo(1)
   *     _ <- foo.foo(100)
   *   } yield (foo.foo _).times == 2 // true
   * }}}
   * */
  def times: Int = delegate.times

  /** Allows to get arguments with which method was executed.
   *  Returns multiple arguments as tuple.
   *  One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.foo.returnsIO(x => IO(5))
   *     _ <- foo.foo(1)
   *     _ <- foo.foo(100)
   *   yield foo.foo.calls == List(1, 100) // true
   *  }}}
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.foo _).returnsIO(x => IO(5))
   *     _ <- foo.foo(1)
   *     _ <- foo.foo(100)
   *   } yield (foo.foo _).calls == List(1, 100) // true
   *  }}}
   * */
  def calls: List[A] = delegate.calls

  /** Returns true if this method was called before other method. */
  def isBefore(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isBefore(other)

  /** Returns true if this method was called after other method. */
  def isAfter(other: StubbedMethod.Order)(implicit callLog: CallLog): Boolean =
    delegate.isAfter(other)

  /** Returns string representation of method.
   *  Representation currently depends on scala version.
   * */
  def asString: String = delegate.asString

  override def toString: String = asString
}
