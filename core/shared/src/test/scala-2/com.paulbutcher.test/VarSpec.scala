package com.paulbutcher.test

import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec

import scala.reflect.ClassTag
import scala.language.higherKinds

class VarSpec extends AnyFunSpec with MockFactory {

  autoVerify = false

  trait Vars {
    var aVar: String
    var concreteVar = "foo"
  }

  it("mock traits with vars") {
    withExpectations {
      val m = mock[Vars]
      (m.aVar_= _).expects("foo")
      (() => m.aVar).expects().returning("bar")
      m.aVar = "foo"
      assertResult("bar") {
        m.aVar
      }
    }
  }

  it("compile without args") {
    class ContextBounded[T: ClassTag] {
      def method(x: Int): Unit = ()
    }

    val m = stub[ContextBounded[String]]

  }

  it("compile with args") {
    class ContextBounded[T: ClassTag](x: Int) {
      def method(x: Int): Unit = ()
    }

    val m = stub[ContextBounded[String]]

  }

  it("compile with provided explicitly type class") {
    class ContextBounded[T](x: ClassTag[T]) {
      def method(x: Int): Unit = ()
    }

    val m = stub[ContextBounded[String]]

  }

  it("mock type constructor arguments") {
    class WithTC[TC[_]](tc: TC[Int])
    type ID[A] = A
    val foo = stub[WithTC[List]]
    val bar = stub[WithTC[ID]]
  }

  it("mock generic arguments") {
    class WithGeneric[T](t: T)

    val foo = stub[WithGeneric[String]]
    val bar = stub[WithGeneric[Int]]
  }

  it("mock type constructor context bounds") {
    trait Async[F[_]]
    class A[F[_] : Async](val b: B[F])
    class B[F[_] : Async](val c: C[F])
    trait C[F[_]]

    val foo = stub[A[List]]
    val bar = stub[B[List]]
    val baz = stub[C[List]]
  }

}