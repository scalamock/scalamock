---
layout: default
title: Known issues
nav_order: 7
permalink: /known-issues/
has_children: true
---


## Scala 3

## Java classes

Mocking of non-abstract java classes is not available without workaround.

[Issue](https://github.com/scala/scala3/issues/18694)

```java
public class JavaClass {
    public int simpleMethod(String b) { return 4; }
}
```

```scala
val m = mock[JavaClass] // No longer compiles

class JavaClassExtended extends JavaClass

val mm = mock[JavaClassExtended] // should be used instead
```

## Java generics without provided type params

You can't stub such interfaces without workaround

```java
public interface Foo {
    void route(java.util.Map map);
}
```

```scala

trait ScalaFoo extends Foo {
  override def route(map: java.util.Map[_, _]): Unit
}
val m = stub[ScalaFoo]

```