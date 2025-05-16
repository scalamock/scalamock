---
layout: default
title: cats-effect
parent: Stubs
nav_order: 5
permalink: /stubs/cats-effect/
---


# cats-effect integration

To use cats-effect integration - your suite should mixin `org.g.stubs.CatsEffectStubs`

Dependency can be found on [Home Page](/)

It provides:
1. implicit conversions to **StubbedIOMethod** instead of **StubbedMethod**
2. **StubIO** instance, which provides support for functional effect to work correctly

## StubbedIOMethod

`StubbedIOMethod` is a subtype of `StubbedMethod`, which adds some convenient methods returning IO.

{: .important }
> For method without arguments returning IO - you can omit creating a function from `()`.
> So instead of `() => myObj.myMethod` just use `myObj.myMethod`

### succeedsWith and raisesErrorWith

1. **succeedsWith** - returns IO with a successful value
2. **raisesErrorWith** - returns IO with an exception

All these methods return IO itself for convenient use in **for comprehensions**.

These methods are the default way of setting results.

```scala
//> using dep org.typelevel::cats-effect:3.6.1
//> using test.dep org.scalamock::scalamock-cats-effect:7.3.2
//> using test.dep org.typelevel::munit-cats-effect:2.1.0

import org.scalamock.stubs.CatsEffectStubs
import cats.effect.*
import munit.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): IO[User]

class MySuite extends CatsEffectSuite, CatsEffectStubs:
  val userId = 100
  val user = User(userId)

  test("succeedsWith"):
    val userService = stub[UserService]
    for {
      _ <- userService.findUser.succeedsWith(user)
      result <- userService.findUser(userId)
    } yield assertEquals(result, user)
    
  test("raisesErrorWith"):
    val userService = stub[UserService]
    for {
      _ <- userService.findUser.raisesErrorWith(new RuntimeException("test"))
      result <- userService.findUser(userId).attempt
    } yield assertEquals(result, Left(new RuntimeException("test")))
```

### returnsIOWith and returnsIO

If above methods are not enough - you can use:
1. **returnsIOWith** - allows to set result which type should match with method return type. Not only IO can be set here, but this method returns IO.
2. **returnsIO** - allows to set result depending on method arguments. This can be useful if you abstract over your tests or create your stubs per suite.

```scala
//> using dep org.typelevel::cats-effect:3.6.1
//> using test.dep org.scalamock::scalamock-cats-effect:7.3.2
//> using test.dep org.typelevel::munit-cats-effect:2.1.0

import org.scalamock.stubs.CatsEffectStubs
import cats.effect.*
import munit.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): IO[Option[User]]

class MySuite extends CatsEffectSuite, CatsEffectStubs:
  val userId = 100
  val user = User(userId)


  test("returnsIOWith"):
    val userService = stub[UserService]
    for {
      _ <- userService.findUser.returnsIOWith(IO.pure(user))
      result <- userService.findUser(userId)
    } yield assertEquals(result, user)
    
  test("returnsIO"):
    val userService = stub[UserService]
    
    for {
      _ <- userService.findUser.returnsIO:
        case `userId` => IO.pure(Some(user))
        case _ => IO.none
      
      result1 <- userService.findUser(userId)
      result2 <- userService.findUser(userId + 1)
    } yield assertEquals((result1, result2), (Some(user), None))
```

## timesIO and callsIO

Same methods as **times** and **calls**, but return IO, added for convenience, you can still just use **times** and **calls**, it is fully thread-safe.
