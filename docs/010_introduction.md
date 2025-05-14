---
layout: default
title: Introduction
nav_order: 2
permalink: /introduction
---


## Why do we test at all?

When you add new functionality to existing one - you can easily break something already working and cost of fixing it on production is very high.
Failing tests will show you that something that worked stopped working.


## The test pyramid

The test pyramid is a visual representation of a testing strategy that emphasizes 
having a larger number of fast and cheap unit tests at the base, 
fewer less cheap integration tests in the middle,
and even fewer end-to-end tests at the top, which are considered very costly.

The pyramid has three layers:
1. **Unit tests** - these are fast, focused tests, that verify individual components in isolation.
2. **Integration tests** - these tests verify how components work together.
3. **End-to-End tests** - these tests verify how the entire system works together from start to finish.

This approach provides a balance between test coverage, execution speed, and maintenance costs, with the most numerous tests being the fastest and least expensive to maintain

![Test Pyramid](/assets/img/test-pyramid.png)

{: .note }
> There are also tests, which are considered **integration**, but they don't verify how components work together.
> For example database layer tests, where database runs in a container, but this has nothing to do with mocking.


## What is mocking?

Mocking or mock testing is a software testing technique that involves creating simulated components, 
known as test doubles, to mimic the behavior of real components in a system. 
The purpose of such testing is to isolate and evaluate specific parts of a software application by replacing real dependencies.

{: .note }
> Mocking can actually be used in each layer of pyramid, but **scalamock** can be used mostly in **unit tests** and sometimes in **integration tests**.
> You can use mocks in **e2e tests** too, but this can't be done with **scalamock** since you probably have multiple instances of your application in your **QA** environment, so you will need a database for that.


## Types of test doubles

### Dummy

Dummy objects are passed around but never actually used. Usually they are just used to fill parameter lists.

```scala
trait NotificationService:
  def sendSms(phone: Phone): Future[Unit]
  
object DummyNotificationService extends NotificationService:
  def sendSms(phone: Phone): Future[Unit] = Future.unit
```

### Fake

Fake objects actually have working implementations, but usually take some shortcut which makes them not suitable for production.
An in-memory database is a good example.
Usually it is hard to maintain such implementations if something changes.

```scala
trait UserRepository:
  def create(user: User): Unit
  def find(userId: UserId): Option[User]

class FakeUserRepository(users: Map[UserId, User]) extends UserRepository:
  def create(user: User): Future[Unit] =
    users += user.id -> user

  def find(userId: UserId): Future[Option[User]] = 
    users.get(userId)
```
    
### Mock
Mock objects are pre-programmed with expectations which form a specification of the calls they are expected to receive and returned result.

### Stub
Stub objects provide canned answers to calls made during the test and
also record some information based on how they were called,
so it can be verified later.


{: .note }
> Martin Fowler in his article [**Mocks Aren't Stubs**](https://martinfowler.com/articles/mocksArentStubs.html) also mentions `Spy` separately from `Stub`, but
we are combining these two concepts into one. So for us - `Stub` also records some information about calls, which can be used to verify behavior.


## What are we testing at all?

1. That component returns some expected result on some expected input.
2. That component behaves somehow on expected input. For example - sends SMS, but we don't care about how it does it.
3. That multiple internal components behave in some expected order.

{: .note }
> Note that testing with test doubles is a **whitebox** testing. We know some internal structure of code which is used to write tests. 


## Mocks vs Stubs

**Mocks** and **Stubs** can both be used in the same manner.

They both allow you to set a result and they both allow you to verify the behaviour - the difference is **how**.

For mocks:

1. Prepare dependencies (create mock objects for dependencies) and wire testee component with them
2. **Setup expectations** and returned results
3. Run tested system
4. Verify test result

For stubs:
1. Prepare dependencies (create stub objects for dependencies) and wire testee component with them
2. Setup returned results
3. Run tested system
4. **Verify expected behaviour** and test result 


## Scalamock 7

scalamock 7 stubs project tries to handle common pitfalls of mocking and offers only a simple set of features.

1. Stub generation
2. Set method returned result
3. Get number of times method was executed or arguments with which it was executed
4. Simple methods to verify order of calls

Also, you got:
1. Integration with functional effects like `ZIO` and `cats-effect IO`
2. No expectations or argument matchers by default. You can throw an exception on unexpected arguments, but it is up to you and is not recommended
3. No need to integrate with testing frameworks

## What's next?

New shiny stubs or classic scalamock, choice is yours.

<span>
[scalamock 7 stubs](/stubs){: .btn .btn .fs-5 .mb-4 .mb-md-0 .mr-2 }
[Classic scalamock](/classic/){: .btn .btn .fs-5 .mb-4 .mb-md-0 .mr-2 }
<span/>
{: .d-flex .flex-justify-around }
