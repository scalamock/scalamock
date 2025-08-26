package newapi

import org.scalamock.stubs.ZIOStubs
import zio.*
import zio.test.*

object ZIOSpec extends ZIOSpecDefault, ZIOStubs:

  trait Foo:
    def zeroArgsTask: Task[Option[String]]

    def zeroArgsTaskFail: Task[Option[String]]

    def zeroArgsUIO: UIO[Option[String]]

    def zeroArgsIO: IO[Int, Option[String]]

    def zeroArgsIOFail: IO[Int, Option[String]]

    def oneArgTask(x: Int): Task[Option[String]]

    def oneArgTaskFail(x: Int): Task[Option[String]]

    def oneArgUIO(x: Int): UIO[Option[String]]

    def oneArgIO(x: Int): IO[Int, Option[String]]

    def oneArgIOFail(x: Int): IO[Int, Option[String]]

    def twoArgsTask(x: Int, y: String): Task[Option[String]]

    def twoArgsTaskFail(x: Int, y: String): Task[Option[String]]

    def twoArgsUIO(x: Int, y: String): UIO[Option[String]]

    def twoArgsIO(x: Int, y: String): IO[Int, Option[String]]

    def twoArgsIOFail(x: Int, y: String): IO[Int, Option[String]]

    def overloaded(x: Int, y: Boolean): UIO[Int]

    def overloaded(x: String): UIO[Boolean]

    def overloaded: UIO[String]

    def typeArgsOptUIO[A](value: A): UIO[Option[A]]

    def typeArgsOptUIOTwoParams[A](value: A, other: A): Task[Option[A]]

  val foo = stub[Foo]

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("check expectations with zio")(
      test("zero args"):
        for
          _ <- foo.zeroArgsTask.returnsZIOWith(ZIO.none)
          _ <- foo.zeroArgsTask.repeatN(10)
        yield assertTrue(foo.zeroArgsTask.calls.size == 11),
      test("two args and cleanup"):
        for
          _ <- foo.twoArgsIOFail.returnsZIO(_ => ZIO.fail(2))
          _ <- foo.twoArgsIOFail(1, "").orElse(ZIO.none)
          calls <- foo.twoArgsIOFail.callsZIO
          times2 <- foo.zeroArgsTask.timesZIO
          result = assertTrue(
            foo.twoArgsIOFail.calls == List((1, "")),
            foo.zeroArgsTask.calls.isEmpty
          )
        yield result,
      test("one arg"):
        for
          _ <- foo.oneArgIO.returnsZIO(_ => ZIO.none)
          _ <- foo.oneArgIO(1)
          result = assertTrue(
            //foo.oneArgIO.times == 1,
            foo.oneArgIO.calls == List(1)
          )
        yield result,
      test("type args one param"):
        for
          _ <- foo.typeArgsOptUIO[String].returnsZIO(_ => ZIO.some("foo"))
          result <- foo.typeArgsOptUIO[String]("foo")
          fooTypeArgsOptUIO = stubbed(foo.typeArgsOptUIO[String])
        yield assertTrue(
          result.contains("foo"),
          fooTypeArgsOptUIO.times == 1,
          fooTypeArgsOptUIO.calls == List("foo")
        ),
      test("type args two params"):
        for
          _ <- foo.typeArgsOptUIOTwoParams[Int].returnsZIO(_ => ZIO.some(1))
          result <- foo.typeArgsOptUIOTwoParams[Int](1, 2).repeatN(1)
          fooTypeArgsOptUIOTwoParams = stubbed(foo.typeArgsOptUIOTwoParams[Int])
        yield assertTrue(
          result.contains(1),
          fooTypeArgsOptUIOTwoParams.times == 2,
          fooTypeArgsOptUIOTwoParams.calls == List((1, 2), (1, 2))
        )
    ) @@ TestAspect.before(ZIO.succeed(resetStubs())) @@ TestAspect.sequential

