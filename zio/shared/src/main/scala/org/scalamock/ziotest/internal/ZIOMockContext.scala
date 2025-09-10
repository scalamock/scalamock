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
package org.scalamock.ziotest.internal

import org.scalamock.context.{CallLog, MockContext}
import org.scalamock.handlers.{Handlers, OrderedHandlers, UnorderedHandlers}
import org.scalamock.ziotest.ExpectationException
import zio.{IO, UIO, ZIO}

/**
 * Creates mocks for zio-test. Verifies mock calls.
 *
 * You should never create ZIOMockContext manually (and there's no way to do so).
 * It will be created by library code for the user.
 */
sealed trait ZIOMockContext {
  // must be public for RunMockedMacros
  val mockContext: MockContext
  private[ziotest] def initializeExpectations(): UIO[Unit]
  private[ziotest] def verifyExpectations(): IO[ExpectationException, Unit]
  protected[ziotest] def inSequence[T](what: => T): T
  protected[ziotest] def inAnyOrder[T](what: => T): T
}

object ZIOMockContext {

  private[ziotest] class Live extends ZIOMockContext with MockContext {

    val mockContext: MockContext = this

    override type ExpectationException = org.scalamock.ziotest.ExpectationException

    override protected def newExpectationException(message: String, methodName: Option[Symbol]) = {
      // Add two line separators to improve error message display in console.
      // Without separators (need to scroll right to see):
      // Exception in thread "zio-fiber-69" org.scalamock.ziotest.ExpectationException: Unexpected call: <mock-2> YqlClient.groupDataAndInsert(Some(2025-02-04), yt-path)
      //
      // With separators (no scrolling needed):
      // Exception in thread "zio-fiber-69" org.scalamock.ziotest.ExpectationException:
      //
      //    Unexpected call: <mock-2> YqlClient.groupDataAndInsert(Some(2025-02-04), yt-path)
      val sep = System.lineSeparator()
      new ExpectationException(s"$sep$sep$message")
    }

    // copy-paste from org.scalamock.MockFactoryBase
    def initializeExpectations(): UIO[Unit] = ZIO.succeed {
      val initialHandlers = new UnorderedHandlers
      callLog = new CallLog

      expectationContext = initialHandlers
      currentExpectationContext = initialHandlers
    }

    private def clearExpectations(): UIO[Unit] = ZIO.succeed {
      // to forbid setting expectations after verification is done
      callLog = null
      expectationContext = null
      currentExpectationContext = null
    }

    def verifyExpectations(): IO[ExpectationException, Unit] = {
      for {
        _ <- ZIO.succeed(callLog.foreach { call =>
          val _ = expectationContext.verify(call)
        })
        oldCallLog = callLog
        oldExpectationContext = expectationContext
        _ <- clearExpectations()
        _ <- ZIO.when(!oldExpectationContext.isSatisfied) {
          ZIO.fail(
            newExpectationException(s"Unsatisfied expectation:\n\n${errorContext(oldCallLog, oldExpectationContext)}")
          )
        }
      } yield ()

    }

    override protected[ziotest] def inSequence[T](what: => T): T = {
      inContext(new OrderedHandlers)(what)
    }

    override protected[ziotest] def inAnyOrder[T](what: => T): T = {
      inContext(new UnorderedHandlers)(what)
    }

    private def inContext[T](context: Handlers)(what: => T): T = {
      currentExpectationContext.add(context)
      val prevContext = currentExpectationContext
      currentExpectationContext = context
      val r = what
      currentExpectationContext = prevContext
      r
    }
  }
} 