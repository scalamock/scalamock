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

// placed in org.scalamock package to access MockContext
package org.scalamock.clazz.ziotest.macros

import org.scalamock.clazz.MockImpl.MockMaker
import org.scalamock.context.MockContext
import org.scalamock.util.MacroAdapter
import scala.language.existentials

/**
 * Creates mocks that check if an effect was called.
 *
 * Using classic scalamock such code wouldn't cause a test failure:
 *
 * {{{
 * // test
 * val m = mock[Service]
 * (m.f _).expects(42).returning(ZIO.succeed(42))
 *
 * // production
 * for {
 *   _ <- ZIO.unit
 *   _ = service.f(42) // effect is not called!
 * } yield ()
 * }}}
 *
 * This macro validates that the mocked effect is actually called,
 * by wrapping the method in suspendSucceed().
 *
 * How it works. A regular mock generates such code (for simplicity, it's more complex in reality):
 *
 * {{{
 * // inside scalamock
 * var callLog = new CallLog
 *
 * // generated in test, instead of mock[Service]
 * val m = new Mock[Service] {
 *   def f(x: Int): UIO[String] = {
 *     val call = new Call(this, arguments)
 *     callLog += call
 *   }
 * }
 * }}}
 *
 * So when the method is called, it's added to callLog, and scalamock thinks that the method was called.
 *
 * We wrap this mock like this:
 * 
 * {{{
 * val m = new Mock[Service] {
 *   def f(x: Int): UIO[String] = ZIO.suspendSucceed {
 *     val call = new Call(this, arguments)
 *     callLog += call
 *   }
 * }
 * }}}
 *
 * So when the method is called, it's not added to callLog immediately.
 * It's added only when the effect f(x) is actually called.
 */
private[scalamock] object CheckEffectInvocationMacros {

  import MacroAdapter.Context

  def mock[T: c.WeakTypeTag](c: Context)(mockContext: c.Expr[MockContext]): c.Expr[T] = {
    make[T](c)(mockContext)(stub = false, mockName = None)
  }

  def stub[T: c.WeakTypeTag](c: Context)(mockContext: c.Expr[MockContext]): c.Expr[T] = {
    make[T](c)(mockContext)(stub = true, mockName = None)
  }

  def mockWithName[T: c.WeakTypeTag](
      c: Context
    )(mockName: c.Expr[String]
    )(mockContext: c.Expr[MockContext]): c.Expr[T] = {
    make[T](c)(mockContext)(stub = false, mockName = Some(mockName))
  }

  def stubWithName[T: c.WeakTypeTag](
      c: Context
    )(mockName: c.Expr[String]
    )(mockContext: c.Expr[MockContext]): c.Expr[T] = {
    make[T](c)(mockContext)(stub = true, mockName = Some(mockName))
  }

  private def make[T: c.WeakTypeTag](
      c: Context
    )(mockContext: c.Expr[MockContext]
    )(stub: Boolean,
      mockName: Option[c.Expr[String]]): c.Expr[T] = {
    val maker = MockMaker[T](c)(mockContext, stub, mockName)
    val originalTree = maker.make
    val transformedTree = transformAst(c)(originalTree.tree)
    c.Expr[T](transformedTree)
  }

  /**
   * scalamock generates such code (can be seen with println(tree)):
   *
   * {{{
   * {
   *   final class $anon extends org.scalamock.ziotest.ScalamockZIOSpecSpec.Service {
   *     def <init>() = {
   *       super.<init>();
   *       ()
   *     };
   *     val mock$special$mockName = factory$macro$1.get[org.scalamock.ziotest.internal.ZIOMockFactory]((zio.`package`.Tag.apply[org.scalamock.ziotest.internal.ZIOMockFactory]((izumi.reflect.Tag.apply[org.scalamock.ziotest.internal.ZIOMockFactory](classOf[org.scalamock.ziotest.internal.ZIOMockFactory], izumi.reflect.macrortti.LightTypeTag.parse[Nothing]((-1099772752: Int), ("\u0004\u0000\u0001*org.scalamock.ziotest.internal.ZIOMockFactory\u0001\u0001": String), ("\u0000\u0000\u0000": String), (30: Int))): izumi.reflect.Tag[org.scalamock.ziotest.internal.ZIOMockFactory])): zio.Tag[org.scalamock.ziotest.internal.ZIOMockFactory])).mockContext.generateMockDefaultName("mock").name;
   *     override def f(x: Int): zio.UIO[String] = $anon.this.mock$f$0.apply(x);
   *     val mock$f$0: MockFunction1[Int, zio.UIO[String]] = new MockFunction1[Int, zio.UIO[String]](factory$macro$1.get[org.scalamock.ziotest.internal.ZIOMockFactory]((zio.`package`.Tag.apply[org.scalamock.ziotest.internal.ZIOMockFactory]((izumi.reflect.Tag.apply[org.scalamock.ziotest.internal.ZIOMockFactory](classOf[org.scalamock.ziotest.internal.ZIOMockFactory], izumi.reflect.macrortti.LightTypeTag.parse[Nothing]((-1099772752: Int), ("\u0004\u0000\u0001*org.scalamock.ziotest.internal.ZIOMockFactory\u0001\u0001": String), ("\u0000\u0000\u0000": String), (30: Int))): izumi.reflect.Tag[org.scalamock.ziotest.internal.ZIOMockFactory])): zio.Tag[org.scalamock.ziotest.internal.ZIOMockFactory])).mockContext, scala.Symbol.apply(scala.Predef.augmentString("<%s> %s%s.%s%s").format($anon.this.mock$special$mockName, "Service", "", "f", "")))
   *   };
   *   new $anon()
   * }.asInstanceOf[org.scalamock.ziotest.ScalamockZIOSpecSpec.Service]
   * }}}
   *
   * We traverse the tree, find the body of mocked ZIO methods, and then wrap them in suspendSucceed.
   */
  private def transformAst(c: Context)(tree: c.Tree): c.Tree = {
    import c.universe._

    object transformer extends Transformer {
      override def transform(tree: Tree): Tree = tree match {
        // match this part of the generated code:
        // override def f(x: Int): zio.UIO[String] = $anon.this.mock$f$0.apply(x);
        case DefDef(mods, name, tparams, vparamss, tpt, rhs) if isZIOType(c)(tpt.tpe) =>
          DefDef(mods, name, tparams, vparamss, tpt, q"_root_.zio.ZIO.suspendSucceed($rhs)")
        case _ => super.transform(tree)
      }
    }

    transformer.transform(tree)
  }

  private def isZIOType(c: Context)(tpe: c.Type): Boolean = {
    tpe != null && tpe.baseClasses.exists(_.fullName == "zio.ZIO")
  }

}
