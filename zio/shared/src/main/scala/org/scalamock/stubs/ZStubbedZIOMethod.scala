package org.scalamock.stubs
import zio._

trait ZStubbedZIOMethod[-REnv, Args, Zio] {
  protected def delegateZIO: URIO[REnv, StubbedMethod[Args, Zio]]

  /** Allows to set result for method with arguments. Returns ZIO
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.returnsZIO((x, y) => ZIO.succeed(1))
   *   yield ()
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO((x, y) => ZIO.succeed(1))
   *   } yield ()
   *  }}}
   * */
  def returnsZIO(f: Args => Zio): URIO[REnv, Unit] = 
    delegateZIO.flatMap(d =>
      ZIO.succeed(d.returns(f))
    )

  /** Allows to set result for method with arguments. Returns ZIO
   *
   * Scala 3
   * {{{
   *   for
   *     _ <- foo.bar.returnsZIOWith(ZIO.succeed(1))
   *   yield ()
   *   }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIOWith(ZIO.succeed(1))
   *   } yield ()
   * }}}
   * */
  def returnsZIOWith(value: => Zio): URIO[REnv, Unit] = returnsZIO(_ => value)

  /** Allows to set success for method with arguments. Returns ZIO
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
  def succeedsWith[Success](result: Success)(implicit ev: UIO[Success] <:< Zio): URIO[REnv, Unit] =
    returnsZIO(_ => ev(ZIO.succeed(result)))

  /** Allows set fail result for method with arguments. Returns ZIO
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.failsWith("foo")
   *   yield ()
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).failsWith("foo")
   *   } yield ()
   *  }}}
   * */
  def failsWith[Failure](result: Failure)(implicit ev: IO[Failure, Nothing] <:< Zio): URIO[REnv, Unit] = {
    returnsZIO(_ => ev(ZIO.fail(result)))
  }

  /** Allows set die result for method with arguments. Returns ZIO
   *
   *  Scala 3
   *  {{{
   *   for
   *     _ <- foo.bar.diesWith(new Exception("foo"))
   *   yield ()
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).diesWith(new Exception("foo"))
   *   } yield ()
   *  }}}
   * */
  def diesWith(ex: => Throwable)(implicit ev: UIO[Nothing] <:< Zio): URIO[REnv, Unit] =
    returnsZIO(_ => ev(ZIO.die(ex)))

  /** Allows to get number of times method was executed. Returns ZIO
   *
   *  Scala 3
   * {{{
   *    for
   *      _ <- foo.bar.returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- foo.bar.timesZIO
   *    yield barTimes == 11 // true
   * }}}
   *  Scala 2
   *  {{{
   *    for {
   *      _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- (foo.bar _).timesZIO
   *    } yield barTimes == 11 // true
   * }}}
   * */
  def timesZIO: URIO[REnv, Int] = delegateZIO.flatMap(d => ZIO.succeed(d.times))

  /** Allows to get number of times method was executed with specific arguments. Returns ZIO
   *
   *  Scala 3
   *  {{{
   *    for
   *      _ <- foo.bar.returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- foo.bar.timesZIO((1, "foo"))
   *    yield barTimes == 11 // true
   * }}}
   *
   *  Scala 2
   *  {{{
   *    for {
   *      _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(1))
   *      _ <- foo.bar(1, "foo").repeatN(10)
   *      barTimes <- (foo.bar _).timesZIO((1, "foo"))
   *    } yield barTimes == 11 // true
   * }}}
   * */
  def timesZIO(args: Args): URIO[REnv, Int] = delegateZIO.flatMap(d => ZIO.succeed(d.times(args)))

  /** Allows to get arguments with which method was executed. Returns ZIO
   *
   *  Returns multiple arguments as tuple. One list item per call.
   *
   *  Scala 3
   *  {{{
   *   for {
   *     _ <- foo.bar.returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- foo.bar.callsZIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   *
   *  Scala 2
   *  {{{
   *   for {
   *     _ <- (foo.bar _).returnsZIO(_ => ZIO.succeed(5))
   *     _ <- foo.bar(1, "foo")
   *     _ <- foo.bar(2, "bar")
   *     calls <- (foo.bar _).callsZIO
   *   } yield calls == List((1, "foo"), (2, "bar")) // true
   *  }}}
   * */
  def callsZIO: URIO[REnv, List[Args]] = delegateZIO.flatMap(d => ZIO.succeed(d.calls))
}

object ZStubbedZIOMethod {

  trait HasStringId[-REnv] { self: ZStubbedZIOMethod[REnv, _, _] =>
    val asString: URIO[REnv, String] = self.delegateZIO.map(_.asString)
  }

  trait Order[-REnv] { self: HasStringId[REnv] =>
    def isBefore[R <: REnv](other: HasStringId[R]): URIO[R, Boolean] = ZCallLog.isBefore(self, other)

    def isAfter[R <: REnv](other: HasStringId[R]): URIO[R, Boolean] = ZCallLog.isAfter(self, other)
  }

}
