---
layout: default
title: ZIO Test
parent: Classic
nav_order: 2
permalink: /classic/zio-test/
---

# ZIO Test Integration

Scalamock provides first-class integration with ZIO Test, offering a powerful and type-safe way to mock ZIO effects. This integration combines the familiar classic mocking style with ZIO-specific enhancements that make testing functional effects both convenient and safer.

<!-- TOC -->
* [ZIO Test Integration](#zio-test-integration)
  * [Setup](#setup)
  * [ZIO-Specific Mock Helpers](#zio-specific-mock-helpers)
  * [Providing Mocks via ZLayer](#providing-mocks-via-zlayer)
  * [Effect Execution Verification](#effect-execution-verification)
  * [ZIO Test Assertion Integration](#zio-test-assertion-integration)
  * [Record-then-Verify Style (Stubs)](#record-then-verify-style-stubs)
  * [ScalamockZIOSyntax for Utilities](#scalamockziosyntax-for-utilities)
  * [Property-Based Testing Integration](#property-based-testing-integration)
  * [Migration from zio-mock](#migration-from-zio-mock)
<!-- TOC -->

---

## Setup

To use scalamock with ZIO Test, you need to add the ZIO integration dependency and extend `org.scalamock.ziotest.ScalamockZIOSpec`:

**Dependency:**
```scala
libraryDependencies += "org.scalamock" %% "scalamock-zio" % "7.5.0" % Test
```

**Test Structure:**
```scala
import org.scalamock.ziotest._
import zio.test._
import zio._

// ScalamockZIOSpec provides everything needed to work with scalamock in zio-test
object ApiServiceSpec extends ScalamockZIOSpec {

  override def spec: Spec[TestEnvironment, Any] =
    suite("ApiServiceSpec")(
      test("return greeting")(
        for {
          // Setup expectations — how mock should be called and what it returns
          _ <- ZIO.serviceWith[UserService] { mock =>
            (mock.getUserName _).expects(4).returnsZIO("Agent Smith")
          }
          // Call code under test
          result <- ZIO.serviceWithZIO[ApiService](_.getGreeting(4))
        } yield assertTrue(result == "Hello, Agent Smith!")
      )
    ).provide(ApiService.layer, mock[UserService]) // Provide required mock for the test
}
```

**Key Differences from Regular scalamock:**

1. **Extends `ScalamockZIOSpec`** instead of mixing in `MockFactory`
2. **Tests run sequentially** to avoid mock interference between test cases
3. **ZIO Environment integration** — mocks are provided through ZIO's dependency injection
4. **Effect execution verification** — automatically ensures ZIO effects are actually executed
5. **ZIO-specific helpers** — additional methods for working with `ZIO.succeed`, `ZIO.fail`, `ZIO.die`

The `ScalamockZIOSpec` provides all the classic scalamock functionality while integrating seamlessly with ZIO Test's execution model.

## ZIO-Specific Mock Helpers

The ZIO integration provides convenient helpers for working with ZIO effects:

### returnsZIO, failsZIO, diesZIO, returnsZIOUnit

```scala
ZIO.serviceWith[UserService] { mock =>
  // Shortcut for returns(ZIO.succeed("Agent Smith"))
  (mock.getName _).expects(4).returnsZIO("Agent Smith")

  // Shortcut for returns(ZIO.fail(new Exception("test")))
  (mock.getName _).expects(4).failsZIO(new Exception("test"))

  // Shortcut for returns(ZIO.die(new RuntimeException("death")))
  (mock.getName _).expects(4).diesZIO(new RuntimeException("death"))

  // Shortcut for returnsZIO(())
  (mock.saveUser _).expects(*).returnsZIOUnit
}
```

You can use all the DSL methods for setting up expectations, just like in regular scalamock for scalatest.

Examples of using this DSL can be found in the [scalamock Classic features documentation](https://scalamock.org/classic/features).

All features from scalamock classic work with the ZIO Test integration:
- Argument Matching
- Ordering
- Call counts
- Returning values
- Throwing exceptions (though for ZIO methods it's better to use `failsZIO()` or `diesZIO()`)
- Call handlers

## Providing Mocks via ZLayer

Use `mock[A]` and `stub[A]` methods that return `ULayer[A]` for integration with ZIO's dependency injection:

```scala
override def spec: Spec[TestEnvironment, Any] =
  suite("ApiServiceSpec")(
    test("return greeting")(
      // Test code that mocks UserService and AuthService...
    )
  ).provide(ApiService.layer, mock[UserService], mock[AuthService])
```

### Fresh mocks for every test

In regular scalamock, it's common to accidentally share mocks between tests, which can cause flaky test behavior due to shared mutable state. The ZIO integration prevents this by creating fresh mock instances for each test automatically.

Every `mock[A]` or `stub[A]` call produces a new instance, ensuring your tests remain isolated and predictable.

### More options

If needed, you can provide mocks through `provideShared()`. Since all tests in `ScalamockZIOSpec` run sequentially, sharing mocks won't lead to race conditions.

You can create stubs using `stub[A]`. Additionally, both `mock[A]` and `stub[A]` can accept a mock name that will be displayed in logs in case of mock call errors.

## Effect Execution Verification

{: .important }
> **Key Feature**: Unlike regular scalamock, the ZIO integration automatically verifies that ZIO effects are actually executed, not just that methods are called.

This prevents bugs where you accidentally forget to execute an effect:

```scala
trait UserService {
  def getUserName(id: Int): UIO[String]
  def setGreeting(greeting: String): UIO[Unit]
}

class ApiService(userService: UserService) {

  def setGreeting(userId: Int): UIO[Unit] =
    for {
      userName <- userService.getUserName(userId)
      greeting = s"Hello, $userName!"
      _ = userService.setGreeting(greeting) // BUG: effect not executed!
    } yield ()
}

test("set greeting for user")(
  for {
    _ <- ZIO.serviceWith[UserService] { mock =>
      (mock.getUserName _).expects(4).returnsZIO("Agent Smith")
      (mock.setGreeting _).expects("Hello, Agent Smith!").returnsZIO(())
    }
    _ <- ZIO.serviceWithZIO[ApiService](_.setGreeting(4))
  } yield assertCompletes
)
```

This test will fail with:

```
Unsatisfied expectation:

Expected:
inAnyOrder {
  <mock-1> UserService.getUserName(4) once (called once)
  <mock-1> UserService.setGreeting(Hello, Agent Smith!) once (never called - UNSATISFIED)
}

Actual:
  <mock-1> UserService.getUserName(4)
```

This allows us to catch bugs caused by forgotten effect execution, unlike regular scalamock for scalatest.

The ZIO integration works automatically for all methods returning `ZIO` (including `Task`, `URIO`, etc.). No additional configuration is required.

You can also mock methods that return non-`ZIO` types. Such mocking works the same as regular scalamock for scalatest — without effect execution verification.

## ZIO Test Assertion Integration

You can use `zio.test.Assertion[A]` directly as mock parameters:

```scala
object MyUtils extends ScalamockZIOSyntax {
  def mockGetName = ZIO.serviceWith[UserService] { mock =>
    (mock.getName _)
      .expects(hasField("id", _.id, equalTo(4)))
      .returnsZIO("Agent Smith")
  }
}
```

This is equivalent to:

```scala
object MyUtils extends ScalamockZIOSyntax {
  def mockGetName = ZIO.serviceWith[UserService] { mock =>
    (mock.getName _)
      .expects(argAssert[Int](_.id shouldBe 4))
      .returnsZIO("Agent Smith")
  }
}
```

**Important:** Under the hood this syntax uses `argAssert`, so mock verification doesn't support unordered calls (except for `Assertion.anything`).

### Unordered Calls with ZIO Assertions

To allow mocks to be called in any order, use `assertion.allowUnorderedCalls`:

```scala
object MyUtils extends ScalamockZIOSyntax {
  /**
    * Works with calls:
    * - service.getName(1)
    * - service.getName(2)
    * 
    * But will fail with (even if using inAnyOrder):
    * - service.getName(2)
    * - service.getName(1)
    */
  def sequence = ZIO.serviceWith[UserService] { mock =>
    (mock.getName _)
      .expects(equalTo(1))
      .returnsZIO("1")
    (mock.getName _)
      .expects(equalTo(2))
      .returnsZIO("2")
  }

  /**
    * Will work with both:
    * - service.getName(1)
    * - service.getName(2)
    * 
    * And:
    * - service.getName(2)
    * - service.getName(1)
    */
  def anyOrder = ZIO.serviceWith[UserService] { mock =>
    (mock.getName _)
      .expects(equalTo(1).allowUnorderedCalls)
      .returnsZIO("1")
    (mock.getName _)
      .expects(equalTo(2).allowUnorderedCalls)
      .returnsZIO("2")
  }
}
```

Note that `allowUnorderedCalls` reduces error reporting quality. When mock call errors occur, it won't show the actual error (why the Assertion returned an error), only the call log.

## Record-then-Verify Style (Stubs)

ZIO Test integration also supports the record-then-verify style:

```scala
object ApiServiceSpec extends ScalamockZIOSpec {

  override def spec: Spec[TestEnvironment, Any] =
    suite("ApiServiceSpec")(
      test("return greeting")(
        for {
          // Setup: define what stub can return (but doesn't have to be called)
          _ <- ZIO.serviceWith[UserService] { stub =>
            (stub.getUserName _).when(*).returnsZIO("Agent Smith")
          }
          // Call code under test
          result <- ZIO.serviceWithZIO[ApiService](_.getGreeting(4))
          // Verify: check that stub was called as expected
          _ <- ZIO.serviceWith[UserService] { stub =>
            (stub.getUserName _).verify(4)
          }
        } yield assertTrue(result == "Hello, Agent Smith!")
      )
    ).provide(ApiService.layer, stub[UserService]) // Provide required stub for the test
}
```

{: .note }
> **Stub Limitation**: If a stub is called unexpectedly, it returns `null`, which may cause `MatchError: null`. Check the stack trace to find the unmocked call.

Example of such an error:

```scala
for {
  _ <- ZIO.serviceWith[Service] { stub =>
    (stub.f _).when(43).returnsZIO("43")
  }
  _ <- ZIO.serviceWithZIO[Service](_.f(42)) // MatchError: null on this line
  _ <- ZIO.serviceWithZIO[Service](_.f(43))
} yield ???
```

## ScalamockZIOSyntax for Utilities

You can extract mock setup logic into utility classes by mixing in `ScalamockZIOSyntax`:

```scala
object MyUtils extends ScalamockZIOSyntax {
  def mockGetName = ZIO.serviceWith[UserService] { mock =>
    (mock.getName _).expects(4).returnsZIO("Agent Smith")
  }
}
```

Mock creation and ordering setup can only be done in test code. All other APIs are available by mixing in `ScalamockZIOSyntax`.

## Property-Based Testing Integration

Scalamock ZIO integration works seamlessly with ZIO Test's property-based testing. Mocks are automatically verified and reset after each generated sample, ensuring test isolation across property iterations.

```scala
object PropertyBasedSpec extends ScalamockZIOSpec {
  override def spec: Spec[TestEnvironment, Any] =
    suite("Property-based tests")(
      test("greeting includes id if getUserName returns it") {
        check(Gen.int(1, 1000)) { randomId =>
          for {
            _ <- ZIO.serviceWith[UserService] { mock =>
              (mock.getUserName _).expects(randomId).returnsZIO(s"User-$randomId")
            }
            result <- ZIO.serviceWithZIO[ApiService](_.getGreeting(randomId))
          } yield assertTrue(result.contains(randomId.toString))
        }
      }
    ).provide(ApiService.layer, mock[UserService])
}
```

The integration handles:
- **Mock verification** after each property iteration
- **Automatic cleanup** between samples  
- **Fresh expectations** for each generated input

This ensures that mock expectations don't leak between property samples and each iteration starts with a clean state.

## Migration from zio-mock

### Migration approaches

#### Approach 1: Move expectations into ZIO comprehensions

This approach results in simpler code but requires more refactoring:

**Before (zio-mock):**
```scala
// Expectations defined in layers
val mockLayer = MockUserService.GetUser(equalTo(123), value(User(123, "John")))
```

**After (scalamock ZIO):**
```scala
// Expectations defined in test logic
for {
  _ <- ZIO.serviceWith[UserService] { mock =>
    (mock.getUser _).expects(123).returnsZIO(User(123, "John"))
  }
  // test logic...
} yield assertion
```

#### Approach 2: Keep expectations in layers (using SimplerZIOMockMigration)

{: .warning }
> **Caution**: `SimplerZIOMockMigration` has some limitations that can introduce bugs in tests. See the class scaladoc for details about these limitations. It's generally recommended to use Approach 1 (move expectations into ZIO comprehensions) for new code.

This approach is closer to the original zio-mock style and involves less refactoring work, but requires understanding additional concepts like `createMockedLayer`:

```scala
// Test with SimplerZIOMockMigration
object ApiServiceSpec extends ScalamockZIOSpec with SimplerZIOMockMigration {

  val userServiceMock = createMockedLayer[UserService] { mock =>
    (mock.getUserName _).expects(4).returnsZIO("Agent Smith")
  }

  override def spec: Spec[TestEnvironment, Any] =
    suite("ApiServiceSpec")(
      test("return greeting") {
        for {
          result <- ZIO.serviceWithZIO[ApiService](_.getGreeting(4))
        } yield assertTrue(result == "Hello, Agent Smith!")
      }
    ).provide(
      ApiService.layer,
      userServiceMock
    )
}
```

**Before (zio-mock):**
```scala
val userServiceMock = MockUserService.GetUser(equalTo(123), value(User(123, "John"))).toLayer
// Used in .provide(userServiceMock)
```

**After (scalamock with SimplerZIOMockMigration):**
```scala
val userServiceMock = createMockedLayer[UserService] { mock =>
  (mock.getUser _).expects(123).returnsZIO(User(123, "John"))
}
// Used in .provide(userServiceMock)
```

### Key differences to consider

1. **API differences** — zio-mock expectations API doesn't map 1-to-1 to scalamock
2. **Programming style** — zio-mock uses a purely functional approach to creating expectations, while scalamock is more imperative

### Migration helpers

**Assertion integration** — `zio.test.Assertion[A]` is integrated into `ScalamockZIOSyntax`, so complex assertions from your existing zio-test code are automatically converted to `MockParameter`. This means you can often reuse existing assertion logic without modification.

**Note:** Under the hood, assertion integration uses `argAssert`, which can reduce error reporting quality compared to direct scalamock assertions.
