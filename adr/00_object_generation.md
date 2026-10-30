# Stub/Mock object generation

Scalamock offers compile-time generation of stubs and mocks.
This document gives more intuition on what is exactly generated.
Approach is very similar for classic scalamock and new stubs.

The idea is to store some state in generated object and update it when expectation is set or method is invoked.
I didn't find any alternatives to this approach, so let's look how it is done exactly.

```scala
trait Foo:
  def oneArg(x: Int): Int
  def twoArgs(x: Int, y: String): String
  def curried(x: Int)(y: String): Int
  

val foo = stub[Foo]
```

## Stubs

### StubbedMethod proxy object

For each method in provided definition scalamock generates a `StubbedMethod[Args, Result]` and stores it internally.

When method is called - scalamock gets corresponding `StubbedMethod` and calls it's `impl` method with provided arguments,
which updates internal state for each corresponding method.

In Scala 3, each method gets an index within its own name group, numbered by erased signature,
and a deferred val takes index 0. Adding an unrelated member therefore changes no existing field
name. Adding another overload can still change a method's index, so configuring a mock through a
supertype whose overloads differ from the mocked class's can reach another member's mock function.

For ordinary Scala types, definitions are emitted in member-name/index order. Native Scala.js types
retain compiler traversal order because their existing JS property dispatcher overwrites earlier
overloads; fixing that dispatch is separate from deterministic member emission. The examples below
use the Scala 3 naming scheme.

Expectations and stub configuration use the overload the compiler already selected in
`(m.foo(_: A))` or `m.foo`. When that symbol is one of the receiver's mockable definitions, it is
used directly, independent of emission order. When it is a member of the receiver that cannot be
mocked, such as a `final` overload, configuration is a compile error rather than a binding to a
sibling. A function configured for a selected definition must still cover its full
parameter list, with each argument type conforming to its parameter: a function that drops a
`using` or defaulted parameter, fixes an argument, or relies on widening is a compile error: the
macro checks the configured function against the selected method's parameters.

The receiver's class may not list the selected symbol at all, for example when the receiver is
typed through an opaque alias's bound and the class implements the method with an override or
with a member inherited from another parent. Then the same-name member whose type, as seen from the
receiver, equals the selection's is used; a sibling overload cannot share that type. If no member
has exactly that type, the earlier lookup by name and argument types runs, and it can bind a
related overload when argument types do not tell the overloads apart.

If there are multiple arguments - scalamock tuples them.

```scala
val foo = new Foo:
  val stub$curried$0 = StubbedMethod[Any, Any](...)
  val stub$oneArg$0 = StubbedMethod[Any, Any](...)
  val stub$twoArgs$0 = StubbedMethod[Any, Any](...)
  
  override def oneArg(x: Int): Int =
    stub$oneArg$0.impl(x).asInstanceOf[Int]
    
  override def twoArgs(x: Int, y: String): String =
    stub$twoArgs$0.impl((x, y)).asInstanceOf[String]
    
  override def curried(x: Int)(y: String): Int =
    stub$curried$0.impl((x, y)).asInstanceOf[Int]
```

Note that internally scalamock stores `StubbedMethod[Args, Result]` as `StubbedMethod[Any, Any]` to simplify object generation.

Result is then casted to correct result type.

---

Each stub has a unique index per suite.

Each `StubbedMethod` has an overriden `toString` method, which is build from unique stub index, type name and method signature.

In scala 3 string method representation is taken from compiler.

This is needed to simplify debugging in failed test cases and call log.

Example:
```
<stub-0> Foo.oneArg(x: Int)Int
```

### Ability to reset a stub

Each stub generates a `stubs$macro$clear()` method that clears all recorded calls and results.

It calls `clear()` method on each `StubbedMethod`.

This is used in `Stubs` trait to reset all stubs if you want to reuse your stubs in your suite.

In such case your test cases should run sequentially.

```scala
val foo = new Foo:
  def stubs$macro$clear(): Unit =
    stub$curried$0.clear()
    stub$oneArg$0.clear()
    stub$twoArgs$0.clear()
```

## Classic

Classic scalamock offers different public API which is built for exact number of arguments.

```scala
foo.oneArg.expects(*)
foo.twoArgs.expects(*, *)
```

This restricts us internally and instead of one `StubbedMethod` we have
`MockFunction0[Result]`, `MockFunction1[Arg0, Result]`, `MockFunction2[Arg0, Arg1, Result]`, up to `MockFunction22[Arg0, ..., Arg21, Result]`.

Or corresponding `StubFunctionN` for stubs.

So internally it looks like this:

```scala
val foo = new Foo:
  val mock$curried$0 = MockFunction2[Int, String, Int](...)
  val mock$oneArg$0 = MockFunction1[Int, Int](...)
  val mock$twoArgs$0 = MockFunction2[Int, String, String](...)

  override def oneArg(x: Int): Int =
    mock$oneArg$0.apply(x).asInstanceOf[Int]
    
  override def twoArgs(x: Int, y: String): String =
    mock$twoArgs$0.apply(x, y).asInstanceOf[String]
    
  override def curried(x: Int)(y: String): Int =
    mock$curried$0.apply(x, y).asInstanceOf[Int]
```

Probably this can be refactored to use internally something more generic, but returning correct type for public API.





















