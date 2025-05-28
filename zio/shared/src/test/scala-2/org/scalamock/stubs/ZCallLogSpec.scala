package org.scalamock.stubs

import org.scalamock.stubs.internal.CallLogOnFiberRef
import zio._
import zio.test._

object ZCallLogSpec extends ZIOSpecDefault with ZIOStubs {
  val getCallLog: UIO[List[String]] = CallLogOnFiberRef.calledMethodsRef.get.map(_.toList)
  val first = stubZIO[FirstTrait]
  val second = stubZIO[SecondTrait]
  val stubbing = ZLayer(
    first.stubbed(_.foo _).succeedsWith(0) *>
      first.stubbed(_.foo2 _).succeedsWith(0) *>
      second.stubbed(_.bar _).succeedsWith("1")
  )

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ZCallLog")(
    test("verify sequential run") {
      for {
        _ <- ZIO.serviceWithZIO[FirstTrait](_.foo(0, 0))
        _ <- ZIO.serviceWithZIO[SecondTrait](_.bar("1"))
        _ <- ZIO.serviceWithZIO[FirstTrait](_.foo2(2))
        //        _ <- ZIO.serviceWithZIO[SecondTrait](_.bar0())
        //        _ <- ZIO.serviceWithZIO[SecondTrait](_.bar00)
        _ <- ZIO.serviceWithZIO[FirstTrait](_.foo(1, 1))

        c1 <- second.stubbed(_.bar _).isBefore(first.stubbed(_.foo2 _))
        c2 <- second.stubbed(_.bar _).isAfter(first.stubbed(_.foo2 _))
        c3 <- first.stubbed(_.foo2 _).isBefore(second.stubbed(_.bar _))
        c4 <- first.stubbed(_.foo2 _).isAfter(second.stubbed(_.bar _))
        c5 <- first.stubbed(_.foo _).isBefore(second.stubbed(_.bar _))
        c6 <- first.stubbed(_.foo _).isAfter(second.stubbed(_.bar _))
        c7 <- second.stubbed(_.bar _).isAfter(first.stubbed(_.foo _))
        c8 <- second.stubbed(_.bar _).isBefore(first.stubbed(_.foo _))

        callLog <- getCallLog
      } yield assertTrue(c1, !c2, !c3, c4, c5, c6, c7, c8, callLog.size == 4)
    },
    test("verify parallel run") {
      val callA = ZIO.serviceWithZIO[FirstTrait](_.foo(0, 0))
      val callB = ZIO.serviceWithZIO[SecondTrait](_.bar("1"))

      val stubA = first.stubbed(_.foo _)
      val stubB = second.stubbed(_.bar _)

      for {
        _ <- callA zipPar callB

        aIsBeforeB <- stubA.isBefore(stubB)
        aIsAfterB <- stubA.isAfter(stubB)

        callLog <- getCallLog
        _ = println(s"Call log: ${callLog.mkString(", ")}")
      } yield assertTrue(
        callLog.size == 2,
        aIsBeforeB != aIsAfterB
      )
    }
  ).provide(first, second, stubbing)

  trait FirstTrait {
    def foo(x: Int, y: Int): UIO[Int]

    def foo2(x: Int): Task[Int]
  }

  trait SecondTrait {
    def bar(x: String): Task[String]

    // ToDo: test after support is added
    def bar0(): Task[String]

    // ToDo: test after support is added
    def bar00: UIO[Int]
  }
}
