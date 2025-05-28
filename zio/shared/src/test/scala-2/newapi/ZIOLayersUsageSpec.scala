package newapi

import newapi.ItemsService.WrongRequestError
import org.scalamock.stubs._
import zio._
import zio.test._

trait Database {
  def persist(id: Int, name: String): Task[Int]

  def get(id: Int): Task[String]
}

class ItemsService(db: Database, cfg: ItemsService.Config) {
  def getItem(id: Int): UIO[Option[String]] =
    db.get(id)
      .when(verifyId(id))
      .catchAll(_ => ZIO.none)


  private def verifyId(id: Int): Boolean = id >= cfg.minId && id < cfg.maxId

  def addItem(id: Int, name: String): IO[ItemsService.Error, Unit] = {
    ZIO.fail(ItemsService.WrongRequestError(s"Wrong id: $id")).unlessDiscard(verifyId(id)) *>
      db.persist(id, name)
        .mapError(ItemsService.UnexpectedError(_))
        .unit
  }
}

object ItemsService {
  sealed trait Error

  case class UnexpectedError(th: Throwable) extends Error

  case class WrongRequestError(reason: String) extends Error

  val layer: URLayer[Database with Config, ItemsService] = ZLayer.derive[ItemsService]

  case class Config(minId: Int, maxId: Int)
}

object ZIOLayersUsageSpec extends ZIOSpecDefault with ZIOStubs {

  val dbStub = stubZIO[Database]
  val getCall = dbStub.stubbed(_.get _)
  val persistCall = dbStub.stubbed(_.persist _)

  val item = "table"

  override def spec: Spec[TestEnvironment with Scope, Any] = {
    suite("ZIO Layers usage (ItemsService test)")(
      test("get item successfully") {
        val id = 150

        for {
          _ <- getCall.succeedsWith(item)
          resp <- ZIO.serviceWithZIO[ItemsService](_.getItem(id))
          calls <- getCall.callsZIO
          pc <- persistCall.timesZIO
          getBeforePersist <- getCall isBefore persistCall
        } yield {
          assertTrue(
            resp.get == item,
            calls.size == 1,
            calls.head == id,
            pc == 0,
            !getBeforePersist
          )
        }
      },
      test("recover exception during getting an item") {
        val id = 150

        for {
          resp <- ZIO.serviceWithZIO[ItemsService](_.getItem(id))
          calls <- getCall.callsZIO
          pc <- persistCall.timesZIO
        } yield {
          assertTrue(
            resp.isEmpty,
            calls.size == 1,
            calls.head == id,
            pc == 0
          )
        }

      }.provideSome[ItemsService & Stub[Database]]( //another way to stub response 
        ZLayer(getCall.failsWith(new Exception("Element does not exist")))
      ),
      test("return None if id is wrong") {
        val id = 10

        for {
          resp <- ZIO.serviceWithZIO[ItemsService](_.getItem(id))
          dc <- getCall.timesZIO
          pc <- persistCall.timesZIO
        } yield assertTrue(
          resp.isEmpty,
          dc == 0,
          pc == 0
        )
      },
      test("persist item successfully") {
        val id = 200

        for {
          _ <- persistCall.succeedsWith(1)
          _ <- ZIO.serviceWithZIO[ItemsService](_.addItem(id, item))
          dc <- getCall.timesZIO
          pc <- persistCall.callsZIO
        } yield {
          assertTrue(
            dc == 0,
            pc.size == 1,
            pc.head == (id, item)
          )
        }
      },
      test("fails with  WrongRequestError if id is wrong ") {
        val id = 10

        for {
          e <- ZIO.serviceWithZIO[ItemsService](_.addItem(id, item)).flip
          dc <- getCall.timesZIO
          pc <- persistCall.timesZIO
        } yield assertTrue(
          e == WrongRequestError("Wrong id: 10"),
          dc == 0,
          pc == 0
        )
      },

    )
  }.provide(
    dbStub,
    ZLayer.succeed(ItemsService.Config(100, 1000)),
    ItemsService.layer
  )

}
