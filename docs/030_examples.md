---
layout: default
title: Examples
nav_order: 5
permalink: /examples
---

# Examples

<!-- TOC -->
* [Examples](#examples)
  * [Stubs](#stubs)
    * [Future + scalatest](#future--scalatest)
    * [ZIO + zio-test](#zio--zio-test)
    * [cats-effect + munit](#cats-effect--munit)
  * [Classic](#classic)
    * [Future + scalatest](#future--scalatest-1)
<!-- TOC -->


All examples are made using **scala 3** (except for braces, markdown still not supports braceless syntax), if you use **scala 2** - please adjust your code accordingly.

## Stubs

### Future + scalatest

We have a **UserAuthService**, which we want to test. It depends on **UserService** and **PasswordService**.

Please look closely in the code below before we proceed to writing tests:

```scala
import scala.concurrent.Future

enum UserStatus {
  case Normal, Blocked
}

enum FailedAuthResult {
  case UserNotFound, UserNotAllowed, WrongPassword
}

case class User(id: Long, status: UserStatus)

trait UserService {
  def findUser(userId: Long): Future[Option[User]]
}

trait PasswordService {
  def checkPassword(id: Long, password: String): Future[Boolean]
}

class UserAuthService(
   userService: UserService,
   passwordService: PasswordService
) {
  def authorize(id: Long, password: String): Future[Either[FailedAuthResult, Unit]] =
    userService.findUser(id).flatMap {
      case None =>
        Future.successful(Left(FailedAuthResult.UserNotFound))

      case Some(user) if user.status == UserStatus.Blocked =>
        Future.successful(Left(FailedAuthResult.UserNotAllowed))

      case Some(user) =>
        passwordService.checkPassword(id, password).map {
          case true => Right(())
          case false => Left(FailedAuthResult.WrongPassword)
        }
    }
}
```

Given such code - we can see 6 possible outcomes. Look at code closely to find them.

1. happy path. user found, not blocked and password is correct
2. find user future fails with an exception
3. user not found
4. user blocked
5. password check future fails with an exception
6. password is wrong

Now let's write all these test cases using **scalamock**.


```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalamock.stubs.Stubs
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future

class UserAuthServiceSpec extends AnyFunSuite, Matchers, Stubs, ScalaFutures {
  // fixture
  class Env {
    val userService = stub[UserService]
    val passwordService = stub[PasswordService]
    val authService = UserAuthService(userService, passwordService)
  }

  // test data
  val userId = 100
  val user = User(userId, UserStatus.Normal)
  val blockedUser = User(userId, UserStatus.Blocked)
  val password = "password"
  
  describe("authorize") {
    it("happy path") {
      val env = Env()
      
      env.userService.findUser.returnsWith(Future.successful(Some(user)))
      env.passwordService.checkPassword.returnsWith(Future.successful(true))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Right(())
      // here you can optionally check that methods were called with correct arguments or correct number of times
      // you shouldn't do it always, only when it makes any sense. Here I think it is not very useful.
      env.userService.findUser.calls shouldBe List(userId)
      env.passwordService.checkPassword.times shouldBe 1
    }
    it("user not found") {
      val env = Env()
      
      env.userService.findUser.returnsWith(Future.successful(None))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.UserNotFound)
    }
    it("user blocked") {
      val env = Env()
      
      env.userService.findUser.returnsWith(Future.successful(Some(blockedUser)))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.UserNotAllowed)
    }
    it("user search failed") {
      val env = Env()
      val ex = new RuntimeException("test")
      
      env.userService.findUser.returnsWith(Future.failed(ex))
      
      val result = env.authService.authorize(userId, password).failed.futureValue
      result shouldBe ex
    }
    it("password check failed") {
      val env = Env()
      val ex = new RuntimeException("test")
      
      env.userService.findUser.returnsWith(Future.successful(Some(user)))
      env.passwordService.checkPassword.returnsWith(Future.failed(ex))
      
      val result = env.authService.authorize(userId, password).failed.futureValue
      result shouldBe ex
    }
    it("wrong password") {
      val env = Env()
      
      env.userService.findUser.returnsWith(Future.successful(Some(user)))
      env.passwordService.checkPassword.returnsWith(Future.successful(false))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.WrongPassword)
    }
  }
}
```

### ZIO + zio-test

We have a **UserAuthService** which we want to test. It depends on **UserService** and **PasswordService**.

Please look closely in the code below before we proceed to writing tests:

```scala
import zio.*

enum UserStatus {
  case Normal, Blocked
}

enum FailedAuthResult {
  case UserNotFound, UserNotAllowed, WrongPassword
}

case class User(id: Long, status: UserStatus)

trait UserService {
  def findUser(userId: Long): Task[Option[User]]
}

trait PasswordService {
  def checkPassword(id: Long, password: String): Task[Boolean]
}

class UserAuthService(
   userService: UserService,
   passwordService: PasswordService
) {
  def authorize(id: Long, password: String): IO[FailedAuthResult | Throwable, Unit] =
    userService.findUser(id).flatMap {
      case None =>
        ZIO.fail(FailedAuthResult.UserNotFound)

      case Some(user) if user.status == UserStatus.Blocked =>
        ZIO.fail(FailedAuthResult.UserNotAllowed)

      case Some(user) =>
        passwordService.checkPassword(id, password)
          .filterOrFail(identity)(FailedAuthResult.WrongPassword)
          .unit
    }
}
```

Given such code - we can see 6 possible outcomes. Look at code closely to find them.

1. happy path. user found, not blocked and password is correct
2. find user fails with an exception
3. user not found
4. user blocked
5. password check fails with an exception
6. password is wrong

Now let's write all these test cases using **scalamock**.

```scala
//> using dep dev.zio::zio:2.1.17
//> using test.dep dev.zio::zio-test:2.1.17
//> using test.dep org.scalamock::scalamock-zio:7.3.2

import org.scalamock.stubs.ZIOStubs
import zio.test.*

object UserAuthServiceSpec extends ZIOSpecDefault, ZIOStubs {
  // fixture
  class Env {
    val userService = stub[UserService]
    val passwordService = stub[PasswordService]
    val authService = UserAuthService(userService, passwordService)
  }

  // test data
  val userId = 100
  val user = User(userId, UserStatus.Normal)
  val blockedUser = User(userId, UserStatus.Blocked)
  val password = "password"

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("authorize")(
      test("happy path") {
        val env = Env()

        for {
          _ <- env.userService.findUser.succeedsWith(Some(user))
          _ <- env.passwordService.checkPassword.succeedsWith(true)
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Right(()))
      },
      test("user not found") {
        val env = Env()

        for {
          _ <- env.userService.findUser.succeedsWith(None)
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Left(FailedAuthResult.UserNotFound))
      },
      test("user blocked") {
        val env = Env()

        for {
          _ <- env.userService.findUser.succeedsWith(Some(blockedUser))
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Left(FailedAuthResult.UserNotAllowed))
      },
      test("user search failed") {
        val env = Env()
        val ex = new RuntimeException("test")

        for {
          _ <- env.userService.findUser.failsWith(ex)
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Left(ex))
      },
      test("password check failed") {
        val env = Env()
        val ex = new RuntimeException("test")

        for {
          _ <- env.userService.findUser.succeedsWith(Some(user))
          _ <- env.passwordService.checkPassword.failsWith(ex)
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Left(ex))
      },
      test("wrong password") {
        val env = Env()

        for {
          _ <- env.userService.findUser.succeedsWith(Some(user))
          _ <- env.passwordService.checkPassword.succeedsWith(false)
          result <- env.authService.authorize(userId, password).either
        } yield assertTrue(result == Left(FailedAuthResult.WrongPassword))
      }
    )
}
```


### cats-effect + munit

We have a **UserAuthService**, which we want to test. It depends on **UserService** and **PasswordService**.

Please look closely in the code below before we proceed to writing tests:

```scala
//> using dep org.typelevel::cats-effect:3.6.1
//> using test.dep org.scalamock::scalamock-cats-effect:7.3.2
//> using test.dep org.typelevel::munit-cats-effect:2.1.0

import cats.effect.IO

enum UserStatus {
  case Normal, Blocked
}

enum FailedAuthResult {
  case UserNotFound, UserNotAllowed, WrongPassword
}

case class User(id: Long, status: UserStatus)

trait UserService {
  def findUser(userId: Long): IO[Option[User]]
}

trait PasswordService {
  def checkPassword(id: Long, password: String): IO[Boolean]
}

class UserAuthService(
   userService: UserService,
   passwordService: PasswordService
) {
  def authorize(id: Long, password: String): IO[Either[FailedAuthResult, Unit]] =
    userService.findUser(id).flatMap {
      case None =>
        IO.pure(Left(FailedAuthResult.UserNotFound))

      case Some(user) if user.status == UserStatus.Blocked =>
        IO.pure(Left(FailedAuthResult.UserNotAllowed))

      case Some(user) =>
        passwordService.checkPassword(id, password).map {
          case true => Right(())
          case false => Left(FailedAuthResult.WrongPassword)
        }
    }
}
```

Given such code - we can see 6 possible outcomes. Look at code closely to find them.

1. happy path. user found, not blocked and password is correct
2. find user fails with an exception
3. user not found
4. user blocked
5. password check fails with an exception
6. password is wrong

Now let's write all these test cases using **scalamock**.

```scala
//> using dep org.typelevel::cats-effect:3.6.1
//> using test.dep org.scalamock::scalamock-cats-effect:7.3.2
//> using test.dep org.typelevel::munit-cats-effect:2.1.0

import org.scalamock.stubs.CatsEffectStubs
import cats.effect.*
import munit.*

object UserAuthServiceSpec extends CatsEffectSuite, CatsEffectStubs {
  // fixture
  class Env {
    val userService = stub[UserService]
    val passwordService = stub[PasswordService]
    val authService = UserAuthService(userService, passwordService)
  }

  // test data
  val userId = 100
  val user = User(userId, UserStatus.Normal)
  val blockedUser = User(userId, UserStatus.Blocked)
  val password = "password"

  test("happy path") {
    val env = Env()

    for {
      _ <- env.userService.findUser.succeedsWith(Some(user))
      _ <- env.passwordService.checkPassword.succeedsWith(true)
      result <- env.authService.authorize(userId, password)
    } yield assertEquals(result, Right(()))
  }
  test("user not found") {
    val env = Env()

    for {
      _ <- env.userService.findUser.succeedsWith(None)
      result <- env.authService.authorize(userId, password)
    } yield assertEquals(result, Left(FailedAuthResult.UserNotFound))
  }
  test("user blocked") {
    val env = Env()

    for {
      _ <- env.userService.findUser.succeedsWith(Some(blockedUser))
      result <- env.authService.authorize(userId, password)
    } yield assertEquals(result, Left(FailedAuthResult.UserNotAllowed))
  }
  test("user search failed") {
    val env = Env()
    val ex = new RuntimeException("test")

    for {
      _ <- env.userService.findUser.raisesErrorWith(ex)
      result <- env.authService.authorize(userId, password).attempt
    } yield assertEquals(result, Left(ex))
  }
  test("password check failed") {
    val env = Env()
    val ex = new RuntimeException("test")

    for {
      _ <- env.userService.findUser.succeedsWith(Some(user))
      _ <- env.passwordService.checkPassword.raisesErrorWith(ex)
      result <- env.authService.authorize(userId, password).attempt
    } yield assertEquals(result, Left(ex))
  }
  test("wrong password") {
    val env = Env()

    for {
      _ <- env.userService.findUser.succeedsWith(Some(user))
      _ <- env.passwordService.checkPassword.succeedsWith(false)
      result <- env.authService.authorize(userId, password)
    } yield assertEquals(result, Left(FailedAuthResult.WrongPassword))
  }
}
```

## Classic

### Future + scalatest

We have a **UserAuthService**, which we want to test. It depends on **UserService** and **PasswordService**.

Please look closely in the code below before we proceed to writing tests:

```scala
import scala.concurrent.Future

enum UserStatus {
  case Normal, Blocked
}

enum FailedAuthResult {
  case UserNotFound, UserNotAllowed, WrongPassword
}

case class User(id: Long, status: UserStatus)

trait UserService {
  def findUser(userId: Long): Future[Option[User]]
}

trait PasswordService {
  def checkPassword(id: Long, password: String): Future[Boolean]
}

class UserAuthService(
   userService: UserService,
   passwordService: PasswordService
) {
  def authorize(id: Long, password: String): Future[Either[FailedAuthResult, Unit]] =
    userService.findUser(id).flatMap {
      case None =>
        Future.successful(Left(FailedAuthResult.UserNotFound))

      case Some(user) if user.status == UserStatus.Blocked =>
        Future.successful(Left(FailedAuthResult.UserNotAllowed))

      case Some(user) =>
        passwordService.checkPassword(id, password).map {
          case true => Right(())
          case false => Left(FailedAuthResult.WrongPassword)
        }
    }
}
```


Given such code - we can see 6 possible outcomes. Look at code closely to find them.

1. happy path. user found, not blocked and password is correct
2. find user future fails with an exception
3. user not found
4. user blocked
5. password check future fails with an exception
6. password is wrong

Now let's write all these test cases using **scalamock**.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalamock.stubs.Stubs
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.Future

class UserAuthServiceSpec extends AnyFunSuite, Matchers, Stubs, ScalaFutures {
  // fixture
  class Env {
    val userService = mock[UserService]
    val passwordService = mock[PasswordService]
    val authService = UserAuthService(userService, passwordService)
  }

  // test data
  val userId = 100
  val user = User(userId, UserStatus.Normal)
  val blockedUser = User(userId, UserStatus.Blocked)
  val password = "password"
  
  describe("authorize") {
    it("happy path") {
      val env = Env()
      
      env.userService.findUser.expects(*).returns(Future.successful(Some(user)))
      env.passwordService.checkPassword.expects(*, *).returns(Future.successful(true))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Right(())
    }
    it("user not found") {
      val env = Env()
      
      env.userService.findUser.expects(*).returns(Future.successful(None))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.UserNotFound)
    }
    it("user blocked") {
      val env = Env()

      env.userService.findUser.expects(*).returns(Future.successful(Some(blockedUser)))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.UserNotAllowed)
    }
    it("user search failed") {
      val env = Env()
      val ex = new RuntimeException("test")

      env.userService.findUser.expects(*).returns(Future.failed(ex))
      
      val result = env.authService.authorize(userId, password).failed.futureValue
      result shouldBe ex
    }
    it("password check failed") {
      val env = Env()
      val ex = new RuntimeException("test")

      env.userService.findUser.expects(*).returns(Future.successful(Some(user)))
      env.passwordService.checkPassword.expects(*, *).returns(Future.failed(ex))
      
      val result = env.authService.authorize(userId, password).failed.futureValue
      result shouldBe ex
    }
    it("wrong password") {
      val env = Env()

      env.userService.findUser.expects(*).returns(Future.successful(Some(user)))
      env.passwordService.checkPassword.expects(*, *).returns(Future.successful(false))
      
      val result = env.authService.authorize(userId, password).futureValue
      result shouldBe Left(FailedAuthResult.WrongPassword)
    }
  }
}
```
