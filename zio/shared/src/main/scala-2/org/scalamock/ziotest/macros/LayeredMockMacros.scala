// Copyright (c) 2011-2025 ScalaMock Contributors (https://github.com/ScalaMock/ScalaMock/graphs/contributors)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.scalamock.ziotest.macros

import java.util.concurrent.locks.ReentrantLock
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import zio.{Scope, Tag, ULayer, URIO, ZIO}

object LayeredMockMacros {

  def mock[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[ULayer[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]

    c.Expr[ULayer[A]](
      q"""
         _root_.zio.ZLayer.succeed {
           _root_.org.scalamock.ziotest.internal.ZIOMockFactory.mock[$tpe](unsafeMockFactory.mockContext)
         }
       """
    )
  }

  def mockWithName[A: c.WeakTypeTag](c: blackbox.Context)(name: c.Expr[String]): c.Expr[ULayer[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]

    c.Expr[ULayer[A]](
      q"""
         _root_.zio.ZLayer.succeed {
           _root_.org.scalamock.ziotest.internal.ZIOMockFactory.mock[$tpe]($name)(unsafeMockFactory.mockContext)
         }
       """
    )
  }

  def stub[A: c.WeakTypeTag](c: blackbox.Context): c.Expr[ULayer[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]

    c.Expr[ULayer[A]](
      q"""
         _root_.zio.ZLayer.succeed {
           _root_.org.scalamock.ziotest.internal.ZIOMockFactory.stub[$tpe](unsafeMockFactory.mockContext)
         }
       """
    )
  }

  def stubWithName[A: c.WeakTypeTag](c: blackbox.Context)(name: c.Expr[String]): c.Expr[ULayer[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]

    c.Expr[ULayer[A]](
      q"""
         _root_.zio.ZLayer.succeed {
           _root_.org.scalamock.ziotest.internal.ZIOMockFactory.stub[$tpe]($name)(unsafeMockFactory.mockContext)
         }
       """
    )
  }

  // Lock is used to make expectations calls for different mocks sequential.
  // Inside scalamock scala.collection.mutable.ListBuffer is used to store expectations, it's not thread-safe.
  // ZIO can create layers in parallel, for example:
  // val mock1 = ZIO.serviceWith[Service1] { mock =>
  //   setExpectations1(mock)
  // }
  //
  // val mock2 = ZIO.serviceWith[Service2] { mock =>
  //   setExpectations2(mock)
  // }
  //
  // testCode.provide(createMockedLayer(mock1), createMockedLayer(mock2))
  //
  // In the example setExpectations1 and setExpectations2 can be called in parallel.
  // To prevent this from losing expectations, we get a lock before each expectations call.
  private val lock = new ReentrantLock()

  // Must be public to be run from a macro.
  // Long name to discourage users from accessing this method.
  val scalamockZiotestInternalWithLock: URIO[Scope, Unit] =
    ZIO.acquireRelease(ZIO.succeed(lock.lock()))(_ => ZIO.succeed(lock.unlock()))

  def createMockedLayer[A: c.WeakTypeTag](
      c: blackbox.Context
    )(expectations: c.Expr[URIO[A, Any]]
    )(tag: c.Expr[Tag[A]]): c.Expr[ULayer[A]] = {
    import c.universe._

    val tpe = weakTypeOf[A]

    c.Expr[ULayer[A]](
      q"""
         _root_.zio.ZLayer.succeed {
           _root_.org.scalamock.ziotest.internal.ZIOMockFactory.mock[$tpe](unsafeMockFactory.mockContext)
         } >+> _root_.zio.ZLayer.fromZIO {
           _root_.zio.ZIO.scoped {
             _root_.org.scalamock.ziotest.macros.LayeredMockMacros.scalamockZiotestInternalWithLock *> $expectations
           }
         }
       """
    )
  }
}
