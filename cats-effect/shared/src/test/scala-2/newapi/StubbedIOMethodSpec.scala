package newapi

import cats.effect.IO
import munit.CatsEffectSuite
import org.scalamock.stubs.{CallLog, CatsEffectStubs}

class StubbedIOMethodSpec extends CatsEffectSuite with CatsEffectStubs {
  // Define test traits
  trait TestTrait0 {
    def noArgs: IO[Int]
    def noArgsWithError: IO[Int]
  }

  trait TestTraitWithArgs {
    def oneArg(x: Int): IO[String]
    def twoArgs(x: Int, y: String): IO[Int]
    def threeArgs(x: Int, y: String, z: Boolean): IO[Int]
  }

  test("StubbedIOMethod0 - returnsIO should set the return value for a method without arguments") {
    val testStub = stub[TestTrait0]
    for {
      _ <- testStub.noArgs.returnsIOWith(IO(42))
      result <- testStub.noArgs
    } yield assertEquals(result, 42)
  }

  test("StubbedIOMethod0 - succeedsWith should set a success value for a method without arguments") {
    val testStub = stub[TestTrait0]
    for {
      _ <- testStub.noArgs.succeedsWith(42)
      result <- testStub.noArgs
    } yield assertEquals(result, 42)
  }

  test("StubbedIOMethod0 - raisesErrorWith should set an error for a method without arguments") {
    val testStub = stub[TestTrait0]
    val exception = new RuntimeException("test exception")
    for {
      _ <- testStub.noArgsWithError.raisesErrorWith(exception)
      result <- testStub.noArgsWithError.attempt
    } yield assert(result.isLeft)
  }

  test("StubbedIOMethod0 - timesIO should return the number of times a method was called") {
    val testStub = stub[TestTrait0]
    for {
      _ <- testStub.noArgs.returnsIOWith(IO(42))
      _ <- testStub.noArgs
      _ <- testStub.noArgs
      _ <- testStub.noArgs
      times <- testStub.noArgs.timesIO
    } yield assertEquals(times, 3)
  }

  test("StubbedIOMethod - returnsIO should set the return value for a method with arguments") {
    val testStub = stub[TestTraitWithArgs]
    for {
      _ <- (testStub.oneArg _).returnsIO(x => IO(s"Value: $x"))
      result <- testStub.oneArg(42)
    } yield assertEquals(result, "Value: 42")
  }

  test("StubbedIOMethod - succeedsWith should set a success value for a method with arguments") {
    val testStub = stub[TestTraitWithArgs]
    for {
      _ <- (testStub.twoArgs _).succeedsWith(42)
      result <- testStub.twoArgs(10, "test")
    } yield assertEquals(result, 42)
  }



  test("StubbedIOMethod - raisesErrorWith should set an error for a method with arguments") {
    val testStub = stub[TestTraitWithArgs]
    val exception = new RuntimeException("test exception")
    for {
      _ <- (testStub.twoArgs _).raisesErrorWith(exception)
      result <- testStub.twoArgs(10, "test").attempt
    } yield assert(result.isLeft)
  }



  test("StubbedIOMethod - timesIO should return the number of times a method was called") {
    val testStub = stub[TestTraitWithArgs]
    for {
      _ <- (testStub.oneArg _).returnsIO(_ => IO("test"))
      _ <- testStub.oneArg(1)
      _ <- testStub.oneArg(2)
      _ <- testStub.oneArg(3)
      times <- (testStub.oneArg _).timesIO
    } yield assertEquals(times, 3)
  }

  test("set result depending on number of call") {
    val testStub = stub[TestTraitWithArgs]
    for {
      _ <- (testStub.oneArg _).returnsIOOnCall {
        case 1 => IO("test")
        case _ => IO("test2")
      }
      res1 <- testStub.oneArg(1)
      res2 <- testStub.oneArg(2)
      res3 <- testStub.oneArg(3)
    } yield {
      assertEquals(res1, "test")
      assertEquals(res2, "test2")
      assertEquals(res3, "test2")
    }
  }
}
