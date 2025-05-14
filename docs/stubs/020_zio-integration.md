---
layout: default
title: ZIO
parent: Stubs
nav_order: 4
permalink: /stubs/zio/
---

# ZIO integration

Before reading this - please read [Features](/stubs/features) page.

The main difference is - you should mixin `org.scalamock.stubs.ZIOStubs` interface instead of `org.scalamock.stubs.Stubs`.

Dependency can be found on [Home Page](/)

It provides:
1. implicit conversions to **StubbedZIOMethod** instead of **StubbedMethod**
2. **StubIO** instance, which provides support for functional effect to work correctly 

## StubbedZIOMethod

`StubbedZIOMethod` is a subtype of `StubbedMethod`, which adds some convenient methods returning ZIO.

### succeedsWith, failWith, diesWith

1. **succeedsWith** - returns ZIO with a successful value
2. **failWith** - returns ZIO with a failed value
3. **diesWith** - returns ZIO with a defect

All these methods return ZIO itself for convenient use in **for comprehensions**.

These methods are the default way of setting a result.

```scala
//> using dep dev.zio::zio:2.1.17
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep dev.zio::zio-test:2.1.17

import org.scalamock.stubs.ZIOStubs
import zio.*
import zio.test.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): IO[None.type, User]

class MySuite extends ZIOSpecDefault, ZIOStubs:
  val userId = 100
  val user = User(userId)

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("tests")(
      test("succeedsWith") {
        val userService = stub[UserService]

        for {
          _ <- userService.findUser.succeedsWith(user)
          result <- userService.findUser(userId)
        } yield assertTrue(result == user)
      },
      test("failWith") {
        val userService = stub[UserService]

        for {
          _ <- userService.findUser.failWith(None)
          result <- userService.findUser(userId).either
        } yield assertTrue(result == Left(None))
      },
      test("diesWith") {
        val userService = stub[UserService]
        val exception = new RuntimeException("test")
        
        for {
          _ <- userService.findUser.diesWith(exception)
          exit <- userService.findUser(userId).exit
        } yield exit match {
          case Exit.Success(_) => assertTrue(false)
          case Exit.Failure(cause) => assertTrue(cause.defects.contains(exception))
        }
      }
    )
```

### returnsZIO and returnsZIOWith

If above methods are not enough - you can use:
1. **returnsZIOWith** - allows to set result which type should match with method return type. Not only ZIO can be set here, but this method returns ZIO.
2. **returnsZIO** - allows to set result depending on method arguments. This can be useful if you abstract over your tests or create your stubs per suite.

```scala
//> using dep dev.zio::zio:2.1.17
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep dev.zio::zio-test:2.1.17

import org.scalamock.stubs.ZIOStubs
import zio.*
import zio.test.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): IO[None.type, User]

class MySuite extends ZIOSpecDefault, ZIOStubs:
  val userId = 100
  val user = User(userId)

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("tests")(
      test("returnsZIOWith") {
        val userService = stub[UserService]

        for {
          _ <- userService.findUser.returnsZIOWith(ZIO.succeed(User(userId)))
          result <- userService.findUser(userId)
        } yield assertTrue(result == user)
      },
      test("returnsZIO") {
        val userService = stub[UserService]

        for {
          _ <- userService.findUser.returnsZIO:
            case `userId` => ZIO.succeed(user)
            case _ => ZIO.fail(None)
            
          result <- userService.findUser(userId)
        } yield assertTrue(result == user)
      }
    )
```

### timesZIO and callsZIO

Same methods as **times** and **calls**, but return ZIO, added for convenience, you can still just use **times** and **calls**, it is fully thread-safe.