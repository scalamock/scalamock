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

package org.scalamock.ziotest

import org.scalamock.handlers.CallHandler
import org.scalamock.matchers.{Matchers, MockParameter}
import org.scalamock.ziotest.internal.{MockContextStub, ZIOAssertionSupport}
import zio.internal.stacktracer.SourceLocation
import zio.test.Assertion
import zio.{IO, UIO, ZIO}

import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
 * Base functionality of ScalamockZIOSyntax, not depending on macros.
 * Contains wrappers for ZIO and syntax for assertions.
 */
private[ziotest] trait ScalamockZIOSyntaxBase extends Matchers with MockContextStub with ZIOAssertionSupport {

  // Wrappers for more convenient mocking of methods returning ZIO.
  // Naming is similar to methods from classic scalamock:
  // - for success return returnsZIO/returningZIO
  // - for error return failsZIO/failingZIO
  // - for defect return dieZIO/dyingZIO
  implicit class RichIOHandler[E, A](val handler: CallHandler[IO[E, A]]) {

    def returnsZIO(value: A): handler.Derived =
      handler.returns(ZIO.succeed(value))

    def returningZIO(value: A): handler.Derived =
      returnsZIO(value)

    def returnsZIOUnit(implicit ev: Unit <:< A): handler.Derived =
      handler.returns(ZIO.unit.map(ev))

    def returningZIOUnit(implicit ev: Unit <:< A): handler.Derived =
      returnsZIOUnit

    def failsZIO(error: E): handler.Derived =
      handler.returns(ZIO.fail(error))

    def failingZIO(error: E): handler.Derived =
      failsZIO(error)

    def diesZIO(error: Throwable): handler.Derived =
      handler.returns(ZIO.die(error))

    def dyingZIO(error: Throwable): handler.Derived =
      diesZIO(error)
  }

  // The compiler doesn't resolve RichIOHandler if the method returns UIO,
  // so we have to explicitly add extension methods for UIO
  implicit class RichUIOHandler[A](val handler: CallHandler[UIO[A]]) {

    def returnsZIO(value: A): handler.Derived =
      handler.returns(ZIO.succeed(value))

    def returningZIO(value: A): handler.Derived =
      returnsZIO(value)

    def diesZIO(error: Throwable): handler.Derived =
      handler.returns(ZIO.die(error))

    def dyingZIO(error: Throwable): handler.Derived =
      diesZIO(error)
  }

  /** Syntax for converting zio.test.Assertion to MockParameter.
    * Internally uses argAssert, so it doesn't work with inAnyOrder.
    *
    * For using with inAnyOrder, use [[AllowAnyOrderSyntax]] with `myAssertion.allowUnorderedCalls`
    *
    * Example:
    *
    * {{{
    * object MyUtils extends ScalamockZIOSyntax {
    *   def mockGetName = ZIO.serviceWith[UserService] { mock =>
    *     (mock.getName _).expects(hasField("id", _.id, equalTo(4))).returnsZIO("Agent Smith")
    *   }
    * }
    * }}}
    *
    * Equivalent to:
    *
    * {{{
    * object MyUtils extends ScalamockZIOSyntax {
    *   def mockGetName = ZIO.serviceWith[UserService] { mock =>
    *     (mock.getName _).expects(argAssert("request")(_.id shouldBe 4)).returnsZIO("Agent Smith")
    *   }
    * }
    * }}}
    *
    * */
  implicit def zioAssertionToMockParameter[A: ClassTag](
      assertion: Assertion[A]
    )(implicit sourceLocation: SourceLocation): MockParameter[A] =
    assertionToMockParameter(assertion, allowAnyOrder = false)

  /**
    * Similar to [[zioAssertionToMockParameter]] with support for arbitrary mock calls. Reports less information on UNSATISFIED errors
    */
  implicit def allowAnyOrderSyntax[A: ClassTag](assertion: Assertion[A]): AllowAnyOrderSyntax[A] =
    new AllowAnyOrderSyntax(assertion)
} 