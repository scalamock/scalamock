---
layout: default
title: Features
parent: Stubs
nav_order: 1
permalink: /stubs/features/
---

# Features

<!-- TOC -->
* [Features](#features)
  * [ETA expansion](#eta-expansion)
  * [Generating a stub](#generating-a-stub)
  * [StubbedMethod](#stubbedmethod)
    * [NotImplementedError](#notimplementederror)
    * [returnsWith](#returnswith)
    * [returns](#returns)
    * [calls and times](#calls-and-times)
    * [isBefore and isAfter](#isbefore-and-isafter)
<!-- TOC -->

---


If you are not using effects systems - all scalamock features are brought into scope via `org.scalamock.stubs.Stubs` which your suite should extend.

There are:
1. `stub[T]` method, which allow macro generation of corresponding objects
2. Implicit conversions from method function representation to different `StubbedMethod`s, which allows to set method results and get invoked method arguments
3. `resetStubs()` method allowing to clear all stubs in case you are using them per suite.

## ETA expansion

Scalamock uses [ETA expansion](https://docs.scala-lang.org/scala3/book/fun-eta-expansion.html) to convert method selection
into a function. And function is converted into corresponding `StubbedMethod` representation via implicit conversion, when some method from it used, e.g. `returnsWith`.

Examples:

```scala
class Foo:
  def noArgs: String = ""
  def oneArg(x: Int): String = ""
  def twoArgs(x: Int, y: String): String = ""

val foo = Foo()

val noArgsFun: () => String = () => foo.noArgs
val oneArgFun: Int => String = foo.oneArg
val twoArgsFun: (Int, String) => String = foo.twoArgs
```

{: .note }
> In Scala 2 you should also add `_` to convert method with arguments to a function.
> E.G. `myObject.myMethod _`

For complex methods you can consult with [FAQ](/faq) page.


## Generating a stub

Method `stub[T]` allows you to generate a stub object for any trait.
It returns `Stub[T]` type which is subtype of `T`. 
If you want to specify type explicitly - you should use only `Stub[T]` and not `T`.

Otherwise, you will get compilation errors while setting up method results.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs

trait MyTrait

class MySpec extends munit.FunSuite, Stubs {
  test("my test case"):
    val myTrait: Stub[MyTrait] = stub[MyTrait]
}
```


## StubbedMethod

StubbedMethod allows you to do 4 simple things:
1. Set up method result. You can also set different result based on arguments
2. Get number of times method was invoked
3. Get method arguments with which it was invoked
4. Check order with some other StubbedMethod

### NotImplementedError

If you don't set up method result it will throw `NotImplementedError` with clear stack trace and method description.
Method description may vary a bit depending on scala version.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]

class MySuite extends munit.FunSuite, Stubs {
  test("findUserExample"):
    val userService = stub[UserService]
    userService.findUser(100)
}
```

Output will be something like:
```
scala.NotImplementedError: Implementation is missing for [<stub-1> UserService.findUser(userId: Long)Option[User]]
	at org.scalamock.stubs.StubbedMethod$Internal.impl(StubbedMethod.scala:273)
	at Playground$MySuite$anon$1.findUser(main.scala:12)
```

### returnsWith

`returnsWith` allows you to set method result no matter what arguments are.

This is the default way if you don't use abstraction for tests or create your stubs per suite.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]

class MySuite extends munit.FunSuite, Stubs:
  val userId = 100
  val user = User(userId)
  
  test("findUserExample"):
    val userService = stub[UserService]
    userService.findUser.returnsWith(Some(user))

    assertEquals(userService.findUser(userId), Some(user))
```

### returns
`returns` allows you to set method result depending on arguments.

This may be useful if you want to abstract over your tests or create your stubs per suite

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]

class MySuite extends munit.FunSuite, Stubs:
  val knownUserId = 100
  val unknownUserId = -1
  val user = User(userId)

  val userService: Stub[UserService] = stub[UserService]

  userService.findUser.returns:
    case `knownUserId` => Some(user)
    case _ => None

  override def beforeEach(context: BeforeEach): Unit =
    resetStubs()

  test("return user for known user id"):
    assertEquals(userService.findUser(knownUserId), Some(user))

  test("not return user for unknown user id"):
    assertEquals(userService.findUser(unknownUserId), None)
```

### returnsOnCall
`returnsOnCall` allows you to set method result depending on call number

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.*

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]

class MySuite extends munit.FunSuite, Stubs:
  val userId = 100
  val user = User(userId)

  val userService: Stub[UserService] = stub[UserService]

  userService.findUser.returnsOnCall:
    case 1 => Some(user)
    case _ => None

  test("return user for known user id"):
    val result1 = userService.findUser(userId)
    val result2 = userService.findUser(userId)
    assertEquals(result1, Some(user))
    assertEquals(result2, None)

```

### calls and times

`calls` returns arguments with which method was invoked, since there might be multiple invocations, it returns a list.

- If method has no arguments - it returns a list of `()` values.
- If method has one argument - it returns a list of that argument type.
- If method has multiple arguments - it returns a list of tupled arguments.

`times` returns number of times method was invoked. This is simply size of `calls`

{: .note }
> Currently `StubbedMethod0[R]` not supports `calls`, in future versions it will become `StubbedMethod[Unit, R]` and will support it.


```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]

class MySuite extends munit.FunSuite, Stubs:
  val userId = 100
  val user = User(userId)
  
  test("findUserExample"):
    val userService = stub[UserService]
    userService.findUser.returnsWith(Some(user))

    userService.findUser(userId)
    userService.findUser(userId)

    assertEquals(userService.findUser.calls, List(userId, userId))
    assertEquals(userService.findUser.times, 2)
```

### isBefore and isAfter

`isBefore` and `isAfter` methods allow you to check order of method invocations.

They use `CallLog` for that which should be defined before `stub` generation as a context parameter.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.*

trait MyService:
  def firstMethod(id: Long): Option[String]
  def secondMethod(phone: String): Unit

class MySuite extends munit.FunSuite, Stubs {

  test("my test case") {
    given CallLog = CallLog()

    val myService = stub[MyService]

    myService.firstMethod.returnsWith(Some("some result"))
    myService.secondMethod.returnsWith(())

    assertEquals(myService.secondMethod.isBefore(myService.firstMethod), false)
    assertEquals(myService.secondMethod.isAfter(myService.firstMethod), true)
  }
}
```

---

You can also print your call log after running your tests to debug order of calls. Just use `toString` for that.

### convert to StubbedMethod explicitly

You can also convert to `StubbedMethod` explicitly using `stubbed` method or adding explicit type annotation `StubbedMethod` and use it later.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs

case class User(id: Long)

trait UserService:
  def findUser(userId: Long): Option[User]
  def eraseAll(): Unit

class MySuite extends munit.FunSuite, Stubs:
  val userId = 100
  val user = User(userId)
  
  test("findUserExample"):
    val userService = stub[UserService]
    val findUserStub = stubbed(userService.findUser)
    //val findUserStub: StubbedMethod[Long, Option[User]] = userService.findUser

    findUserStub.returnsWith(Some(user))

    val result = userService.findUser(userId)

    assertEquals(result, Some(user))
    assertEquals(findUserStub.times, 1)
    assertEquals(findUserStub.calls, List(100))
```

{: .warning  }
> Currently method without arguments is converted to `StubbedMethod0[R]`
> which is deprecated and later on will be replaced with `StubbedMethod[Unit, R]`.
> So you should not explicitly use type annotation for `StubbedMethod0[R]`


### Sharing your stubs or not

By default, stubs should not be shared between your test cases.

Usually you should create some fixture or wiring to be reused in each test-case or your test case should encapsulate this logic inside.

Let’s assume we have a **Greetings** functionality that we want to test.
It will be very simple just to show you usage of fixtures.

```scala
trait Greetings:
  def sayHello(name: String): Unit

object Greetings:
  class Impl(formatter: Formatter):
    def sayHello(name: String): Unit =
      println(formatter.format(name))

  trait Formatter:
    def format(s: String): String

  object EnglishFormatter extends Formatter:
    def format(s: String): String = s"Hello $s"

  object JapaneseFormatter extends Formatter:
    def format(s: String): String =  s"こんにちは $s"
```

Let's now see how we can create a fixture. I'll be using **scalatest** is this example:

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalamock.stubs.Stubs

class FormatterSpec extends AnyFunSuite, Matchers, Stubs:
  class Env: // this is a fixture
    val formatter = stub[Greetings.Formatter]
    val greetings = Greetings.Impl(formatter)

  test("test case 1"):
    val env = Env()
    env.formatter.format.returnsWith("Hello World")

    env.greetings.sayHello("World")

    env.formatter.format.times shouldBe 1
    env.formatter.format.calls shouldBe List("World")

  test("test case 2"):
    val env = Env()
    env.formatter.format.returnsWith("Hello Mama")

    env.greetings.sayHello("Mama")

    env.formatter.format.times shouldBe 1
    env.formatter.format.calls shouldBe List("Mama")
```

---

Still, if you want to create your stubs once and share them between your test-cases - you can do it!

To make possible - you should use `resetStubs()` method before or after each test case and ensure your test cases run sequentially.

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter
import org.scalamock.stubs.Stubs

class FormatterSpec extends AnyFunSuite, Matchers, BeforeAndAfter, Stubs:
  val formatter = stub[Greetings.Formatter]
  val greetings = Greetings.Impl(formatter)
  
  before {
    resetStubs()
  }

  test("test case 1") {
    formatter.format.returnsWith("Not Hello World")

    greetings.sayHello("World") shouldBe "Not Hello World"
    formatter.format.times shouldBe 1
    formatter.format.calls shouldBe List("World")
  }

  test("test case 2") {
    formatter.format.returnsWith("Hello Mama")

    greetings.sayHello("Mama") shouldBe "Hello Mama"

    formatter.format.times shouldBe 1
    formatter.format.calls shouldBe List("Mama")
  }
```

