---
layout: default
title: Features
parent: Classic
nav_order: 1
permalink: /classic/features
---

# Features

<!-- TOC -->
* [Features](#features)
  * [ETA expansion](#eta-expansion)
  * [Mocks](#mocks)
    * [Generate a mock](#generate-a-mock)
    * [Basic expectation](#basic-expectation)
    * [Set returned result](#set-returned-result)
    * [Expectation on number of calls](#expectation-on-number-of-calls)
    * [Expectation on arguments](#expectation-on-arguments)
    * [Argument Capture](#argument-capture)
  * [Stubs](#stubs)
    * [Generate a stub](#generate-a-stub)
    * [Basics](#basics)
    * [Set returned result](#set-returned-result-1)
    * [Verify](#verify)
    * [Verify number of calls](#verify-number-of-calls)
    * [Verify arguments](#verify-arguments)
  * [Ordering](#ordering)
<!-- TOC -->

---

All scalamock features are brought into scope via `org.scalamock.scalatest.MockFactory` and `org.scalamock.specs2.MockContext`
which mix core interface `org.scalamock.MockFactoryBase`.

There are:

1. Implicit conversions from method function representation to different `MockFunction`s and `StubFunction`s.
2. `mock[T]` and `stub[T]` methods, which allow macro generation of corresponding objects
3. Different argument matcher methods like `*`, `~`, `where`
4. Methods allowing to work with ordering like `inSequence`, `inAnyOrder`, `inSequenceWithLogging`, `inAnyOrderWithLogging`
5. Methods allowing to create `MockFunction`s and `StubFunction`s by hand
6. Abstractions allowing to integrate different testing frameworks like `specs2` and `scalatest`

Let's inspect what we can do with it.

## ETA expansion

Scalamock uses [ETA expansion](https://docs.scala-lang.org/scala3/book/fun-eta-expansion.html) to convert method selection
into a function. And function is converted into corresponding `StubFunction/MockFunction` representation via implicit conversion.

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

## Mocks

This is part of scalamock, which is also called **Expectations-First Style**.

### Generate a mock

```scala
trait MyTrait:
  def twoArgs(x: Int, y: String): String
  def oneArg(x: Int): String
  def noArgs: String
 
val myTrait = mock[MyTrait]
```

### Basic expectation

```scala
val myTrait = mock[MyTrait]

myTrait.twoArgs.expects(1, "hello").returns("world")
```

You can read it as:

1. method `myTrait.twoArgs` is expected to be called with arguments `1` and `"hello"`
2. on that call it will return `"world"`
3. it expects exactly one call

**Let's now look more closely into it:**

To setup expectations you should use `expects` method on method of mocked trait.
This method exists on `MockFunction` interface, which we got through implicit conversion when calling `expects`.

It accepts argument matchers as arguments, where number of arguments and their types should match method signature.
For now, we will use literal values, which are implicitly converted to corresponding matchers.

`expects` method also returns an instance of `CallHandler` class which has useful methods allowing to set result
and configure expected number of times for method to be called. By default, expected only 1 call.

### Set returned result

You can set returned by method value using `returns` method on `CallHandler`. It is equivalent to `returning` method, which is also available.

Also, you can use methods `throwing` and `throws` to set thrown exception as result.

Also, you can use `onCall` method to set returned value based on arguments.

```scala
myTrait.twoArgs.expects(1, "hello").returns("world")
myTrait.twoArgs.expects(1, "hello").returning("world")

myTrait.twoArgs.expects(1, "hello").throwing(new RuntimeException("world"))
myTrait.twoArgs.expects(1, "hello").throws(new RuntimeException("world"))

myTrait.twoArgs.expects(1, "hello").onCall { (x: Int, y: String) => s"world, $y-$x" }
myTrait.twoArgs.expects(1, "hello").onCall { (x: Int, y: String) =>
  if (y.length != x)
    throw new RuntimeException("not hello")
  else
    "world"
}
```

### Expectation on number of calls

To set up number of expected calls you can use methods:

- `never()`
- `once()`
- `twice()`
- `anyNumberOfTimes()`
- `atLeastOnce()`
- `atLeastTwice()`
- `noMoreThanOnce()`
- `noMoreThanTwice()`
- `repeated`, which accepts either exact number of times or `Range`

```scala
// method is expected to be called with 1 and "hello" arguments 0 or 1 times
myTrait.twoArgs.expects(1, "hello").noMoreThanOnce()

// method is expected to be called with 1 and "hello" arguments exactly 3 times
myTrait.twoArgs.expects(1, "hello").repeated(3)
// method is expected to be called with 1 and "hello" arguments from 3 to 10 times
myTrait.twoArgs.expects(1, "hello").repeated(3 to 10)
```

### Expectation on arguments

Argument matchers are used to create expectation on arguments. If expectation not met - **scalamock** will throw an exception.

There are:
1. exact argument matchers - literals and variable references like `1`, `"hello"`, `x`, `y`
2. wildcard matcher `*` - matches any value
3. epsilon matcher `~` - matches floating point numbers within 0.001 range 
4. predicate matcher `where` - matches any value that satisfies some predicate


```scala
// expects exact arguments "foo" and 42
myMock.someMethod.expects("foo", 42).returns(()) 

// expects exact argument "foo" and any second argument
myMock.someMethod.expects("foo", *).returns(())      

// expects exact argument "foo" and number close to 42.0 within 0.001 range
// 42.0001 will pass and 42.01 will fail
myMock.otherMethod.expects("foo", ~42.0).returns(())

// def someMethod(x: Int, y: Int): Unit
// expects two arguments, where first is less than second
myMock.someMethod.expects(where { (x, y) => x < y }).returns(())

```

You can also use `where` with as many arguments as you want up to 22.


### Argument Capture

Using the Capture feature in `org.scalamock.matchers.ArgCapture`, 
it is easy and convenient to use wildcard matches but assert on the results later on.

It is possible to store either a single value in a `CaptureOne`, or a `Seq` or values with a `CaptureAll`.
Note that the call to `.value` will throw if nothing was captured. 
Also, the `CaptureOne` will only keep the last value captured.

```scala
  "scalamock" can "capture the arguments of mocks - capture one" in {
    val m = mock[TestTrait]
    val c1 = ArgCapture.CaptureOne[Int]()

    m.oneParam.expects(capture(c1)).returns(()).once()
    m.oneParam(42)
    c1.value shouldBe 42
  }

  "scalamock" can "capture the arguments of mocks - capture all" in {
    val m = mock[TestTrait]
    val c = ArgCapture.CaptureAll[Int]()

    m.oneParam.expects(capture(c)).returns(()).repeat(3)
    m.oneParam(99)
    m.oneParam(17)
    m.oneParam(583)
    c.value shouldBe 583
    c.values shouldBe Seq(99, 17, 583)
  }
```

## Stubs

This is part of scalamock, which is also called **Record-then-Verify Style**.

### Generate a stub

```scala
trait MyTrait:
  def twoArgs(x: Int, y: String): String
  def oneArg(x: Int): String
  def noArgs: String
 
val myTrait = stub[MyTrait]
```

### Basics

```scala
val myTrait = mock[MyTrait]

myTrait.twoArgs.when(*, *).returns("world")
```

Method `myTrait.twoArgs` on any arguments returns `"world"`.
It won't throw an exception if method was not called, this is the main difference from mocks.

**Let's now look more closely into it:**

To set up returned result you should use `when` method on method of stubbed trait.
This method exists on `StubFunction` interface, which we got through implicit conversion when calling `when`.

It accepts argument matchers as arguments, where number of arguments and their types should match method signature.
For now, we will use wildcard values, which allow any value to pass.

`when` method also returns an instance of `CallHandler` class which has useful methods allowing to set result.

.{: .important }
> The default way of working with stubs - is not setting any expectations on number of calls and pass all arguments to `when` as wildcard matchers.
> This differs from mocks, where you usually specify exact arguments.

### Set returned result

You can set returned by method value using `returns` method on `CallHandler`. It is equivalent to `returning` method, which is also available.

Also, you can use methods `throwing` and `throws` to set thrown exception as result.

Also, you can use `onCall` method to set returned value based on arguments.

```scala
myTrait.twoArgs.when(*, *).returns("world")
myTrait.twoArgs.when(*, *).returning("world")

myTrait.twoArgs.when(*, *).throwing(new RuntimeException("world"))
myTrait.twoArgs.when(*, *).throws(new RuntimeException("world"))

myTrait.twoArgs.when(*, *).onCall { (x: Int, y: String) => s"world, $y-$x" }
myTrait.twoArgs.when(*, *).onCall { (x: Int, y: String) =>
  if (y.length != x)
    throw new RuntimeException("not hello")
  else
    "world"
}
```

### Verify

```scala
// run tested functionality

myTrait.twoArgs.verify(1, "hello").once()
```

To verify that method was called with some arguments you can use `verify` method on method of stubbed trait.
This method exists on `StubFunction` interface, which we got through implicit conversion when calling `verify`.

It accepts argument matchers as arguments, where number of arguments and their types should match method signature.
For now, we will use literal values, which are implicitly converted to corresponding matchers.

Note that here we also use `.once()` method, which verifies, that method was called exactly once with such arguments.

If verification fails - **scalamock** will throw an exception with detailed message.

### Verify number of calls

To verify number of calls you can use methods:

- `never()`
- `once()`
- `twice()`
- `anyNumberOfTimes()`
- `atLeastOnce()`
- `atLeastTwice()`
- `noMoreThanOnce()`
- `noMoreThanTwice()`
- `repeated`, which accepts either exact number of times or `Range`

```scala
// method is expected to be called with 1 and "hello" arguments 0 or 1 times
myTrait.twoArgs.verify(1, "hello").noMoreThanOnce()

// method is expected to be called with 1 and "hello" arguments exactly 3 times
myTrait.twoArgs.verify(1, "hello").repeated(3)

// method is expected to be called with 1 and "hello" arguments from 3 to 10 times
myTrait.twoArgs.verify(1, "hello").repeated(3 to 10)
```

### Verify arguments

You can use argument matchers to verify arguments.

There are:
1. exact argument matchers - literals and variable references like `1`, `"hello"`, `x`, `y`
2. wildcard matcher `*` - matches any value
3. epsilon matcher `~` - matches floating point numbers within 0.001 range
4. predicate matcher `where` -  passes if arguments satisfy some predicate

```scala
// expects exact arguments "foo" and 42
myMock.someMethod.verify("foo", 42).once()

// expects exact argument "foo" and any second argument
myMock.someMethod.verify("foo", *).once()      

// expects exact argument "foo" and number close to 42.0 within 0.001 range
// 42.0001 will pass and 42.01 will fail
myMock.otherMethod.verify("foo", ~42.0).once()

// def someMethod(x: Int, y: Int): Unit
// expects two arguments, where first is less than second
myMock.someMethod.verify(where { (x, y) => x < y }).once()
```

## Ordering

There are two method, which allow to work with ordering - `inSequence` and `inAnyOrder` and you can mix them as you wish.

If expectation fails - **scalamock** will throw an exception with detailed message.

```scala
// expect that machine is turned on before turning it off
inSequence {
  (() => machineMock.turnOn).expects().returns(())
  (() => machineMock.turnOff).expects().returns(()) 
}

// players can be fetched in any order
inAnyOrder {
  databaseMock.getPlayerByName.expects("Hans").returns(()) 
  databaseMock.getPlayerByName.expects("Boris").returns(()) 
}
```

Multiple sequences can be specified. As long as the calls within each sequence happen in the correct order, calls within different sequences can be interleaved. For example:

```scala
inSequence {
  mockedFunction.expects(1).returns(())
  mockedFunction.expects(2).returns(())
}
inSequence {
  mockedFunction.expects(3).returns(())
  mockedFunction.expects(4).returns(())
}
```

To specify that there is no constraint on ordering, use `inAnyOrder` (just remember that there is an implicit `inAnyOrder` at the top level). Calls to `inSequence` and `inAnyOrder` can be arbitrarily nested. For example:

```scala
mockedObject.a.expects().returns(())
inSequence {
  mockedObject.b.expects().returns(())
  inAnyOrder {
    mockedObject.c.expects().returns(())
    inSequence {
      mockedObject.d.expects().returns(())
      mockedObject.e.expects().returns(())
    }
    mockedObject.f.expects().returns(())
  }
  mockedObject.g.expects().returns(())
}
```

All the following invocation orders of `mockedObject` methods are correct according to the above specification:

```
a, b, c, d, e, f, g
b, c, d, e, f, g, a
b, c, d, a, e, f, g
a, b, d, f, c, e, g
```