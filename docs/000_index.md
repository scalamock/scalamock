---
layout: default
title: Home
nav_order: 1
permalink: /
---

<img src="/assets/img/scalamock-logo.png" alt="scalamock logo" style="max-width: 55%; height: auto;">
{: .d-flex .flex-justify-around }

---

**Native Scala mocking framework**
{: .text-center .fs-8 .text-grey-dk-300 }

[Introduction](/introduction){: .btn .btn .fs-5 .mb-4 .mb-md-0 .mr-2 }
[Getting started](/#getting-started){: .btn .btn .fs-5 .mb-4 .mb-md-0 .mr-2 }
[View on GitHub](https://github.com/scalamock/scalamock){: .btn .fs-5 .mb-4 .mb-md-0 }
{: .d-flex .flex-justify-around }

---

Supported Platforms
{: .text-center .fs-8 .text-grey-dk-300 }


| Scala Version | JVM | JS (1.x) | Native (0.5.x) |
|---------------|:---:|:--------:|:--------------:|
| 2.12.x        |  ✅  |    ✅     |  Coming soon   |
| 2.13.x        |  ✅  |    ✅     |  Coming soon   |
| 3.x           |  ✅  |    ✅     |  Coming soon   |


---

Dependencies
{: .text-center .fs-8 .text-grey-dk-300 }

![](https://img.shields.io/github/v/release/scalamock/scalamock?color=green])
{: .d-flex .flex-justify-start }

### sbt

```scala
libraryDependencies ++= Seq(
  // core module
  "org.scalamock" %% "scalamock" % "<version in the badge>" % Test,
  // zio integration
  "org.scalamock" %% "scalamock-zio" % "<version in the badge>" % Test,
  // cats-effect integration
  "org.scalamock" %% "scalamock-cats-effect" % "<version in the badge>" % Test
)
```

### Mill

```scala
object main extends JavaModule {
  object test extends JavaModuleTests {
    override def ivyDeps =
      Agg(
        ivy"org.scalamock::scalamock:<version in the badge>",
        // zio integration
        ivy"org.scalamock::scalamock-zio:<version in the badge>",
        // cats-effect integration
        ivy"org.scalamock::scalamock-cats-effect:<version in the badge>"
      )
  }
}
```

### Scala-CLI

```scala
//> using test.dep "org.scalamock::scalamock:<version in the badge>"
//> using test.dep "org.scalamock::scalamock-zio:<version in the badge>"
//> using test.dep "org.scalamock::scalamock-cats-effect:<version in the badge>"
```

### Maven

```xml
<dependencies>
  <!-- core module -->
  <dependency>
    <groupId>org.scalamock</groupId>
    <artifactId>scalamock_3</artifactId>
    <version><!-- version in the badge --></version>
    <scope>test</scope>
  </dependency>
  <!-- zio integration -->
  <dependency>
    <groupId>org.scalamock</groupId>
    <artifactId>scalamock-zio_3</artifactId>
    <version><!-- version in the badge --></version>
    <scope>test</scope>
  </dependency>
  <!-- cats-effect integration -->
  <dependency>
    <groupId>org.scalamock</groupId>
    <artifactId>scalamock-cats-effect_3</artifactId>
    <version><!-- version in the badge --></version>
    <scope>test</scope>
  </dependency>
</dependencies>
```


Getting started
{: .text-center .fs-8 .text-grey-dk-300 }
<a name="getting-started"></a>


### Classic + scalatest

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MyTest extends AnyFlatSpec, MockFactory:
  trait Wiring:
    val service1 = stub[Service1]
    val service2 = mock[Service2]
    val service3 = Service3(service1, service2)

  it should "do something" in new Wiring {
    // service1.someMethod.when(...).returns(...)
    // service2.someMethod.expects(...).returns(...)
    // run tested method
    // service1.someMethod.verify(...).once()
  }
```

### Classic + specs2

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.specs2::specs2-core:5.6.3

import org.scalamock.specs2.MockContext
import org.specs2.mutable.Specification

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MySpec extends Specification {

  trait Wiring extends MockContext {
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)
  }
  
  "CoffeeMachine" should {
    "not turn on the heater when the water container is empty" in new Wiring {
      // service1.someMethod.when(...).returns(...)
      // service2.someMethod.expects(...).returns(...)
      // run tested method
      // service1.someMethod.verify(...).once()
    }
  }
}
```

### Classic + ZIO Test

```scala
//> using dep dev.zio::zio:2.1.19
//> using test.dep dev.zio::zio-test:2.1.19
//> using test.dep org.scalamock::scalamock-zio:7.5.0

import org.scalamock.ziotest._
import zio._
import zio.test._

trait UserService {
  def getUserName(id: Int): Task[String]
}

object UserServiceTest extends ScalamockZIOSpec {
  
  override def spec: Spec[TestEnvironment, Any] =
    suite("UserService tests")(
      test("should return user name") {
        for {
          _ <- ZIO.serviceWith[UserService] { mock =>
            (mock.getUserName _).expects(42).returnsZIO("John")
          }
          userName <- ZIO.serviceWithZIO[UserService](_.getUserName(42))
        } yield assertTrue(userName == "John")
      }.provide(mock[UserService])
    )
}
```

### Stubs + munit

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalameta::munit:1.1.1

import org.scalamock.stubs.Stubs
import munit.FunSuite

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MyTest extends FunSuite, Stubs:
  class Env:
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)


  test("do something"):
    val env = Env()
    // env.service1.someMethod.returnsWith(...)
    // env.service2.someMethod.returnsWith(...)
    // run tested method
    // assertEquals(env.service1.calls, List(...))
    // assertEquals(env.service2.times, ...)
```

### Stubs + scalatest

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.scalatest::scalatest:3.2.19

import org.scalamock.stubs.Stubs
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MyTest extends AnyFunSuite, Matchers, Stubs:
  class Env:
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)

  test("do something"):
    val env = Env()
    // env.service1.someMethod.returnsWith(...)
    // env.service2.someMethod.returnsWith(...)
    // run tested method
    // env.service1.calls shouldBe List(...)
    // env.service2.times shouldBe ...
```

### Stubs + specs2

```scala
//> using test.dep org.scalamock::scalamock:7.3.2
//> using test.dep org.specs2::specs2-core:5.6.3

import org.scalamock.stubs.Stubs
import org.specs2.mutable.Specification

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MySpec extends Specification, Stubs:
  class Env:
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)

  "do something" should {
    "work" in {
      val env = Env()
      // env.service1.someMethod.returnsWith(...)
      // env.service2.someMethod.returnsWith(...)
      // run tested method
      // env.service1.calls check
      // env.service2.times check
    }
  }
```

### Stubs + ZIO test

```scala
//> using dep dev.zio::zio:2.1.17
//> using test.dep dev.zio::zio-test:2.1.17
//> using test.dep org.scalamock::scalamock-zio:7.3.2

import org.scalamock.stubs.ZIOStubs
import zio.test.*

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MyTest extends ZIOSpecDefault, ZIOStubs:
  class Env:
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("tests")(
      test("do something") {
        val env = Env()
        /*
            for {
              _ <- env.service1.someMethod.succeedsWith(...)
              _ <- env.service2.someMethod.succeedsWith(...)
              result <- env.service3 logic to test
            yield assertTrue(
              result == ...,
              env.service1.someMethod.calls == ...,
              env.service2.someMethod.times == ...
            )
         */
      }
    )
```

### Stubs + munit-cats-effect

```scala
//> using dep org.typelevel::cats-effect:3.6.1
//> using test.dep org.scalamock::scalamock-cats-effect:7.3.2
//> using test.dep org.typelevel::munit-cats-effect:2.1.0

import org.scalamock.stubs.CatsEffectStubs
import cats.effect.*
import munit.*

trait Service1
trait Service2
class Service3(service1: Service1, service2: Service2)

class MyTest extends CatsEffectSuite, CatsEffectStubs:
  class Env:
    val service1 = stub[Service1]
    val service2 = stub[Service2]
    val service3 = Service3(service1, service2)

  test("do something"):
    val env = Env()
    /*
        for {
          _ <- env.service1.someMethod.succeedsWith(...)
          _ <- env.service2.someMethod.succeedsWith(...)
          result <- env.service3 logic to test
        } yield {
          assertEquals(result, ...)
          assertEquals(env.service1.someMethod.calls, ...)
          assertEquals(env.service2.someMethod.times, ...)
        }
     */
```


