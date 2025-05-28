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

package org.scalamock.stubs

import org.scalamock.stubs.CallLog.EffectfulAPI

import java.util.concurrent.atomic.AtomicReference

/** Allows to check order of executed methods. Should be declared before stub generation  */
class CallLog {
  /** Use it only for debug purposes. String representation of called methods can change */
  override def toString: String = internal.calledMethods.mkString("\n")

  def effectfulAPI(io: StubIO): EffectfulAPI[io.F] = new EffectfulAPI[io.F] {
    override def calledMethods: io.F[Nothing, Seq[String]] = io.succeed(internal.calledMethods)

    override def write(methodName:  String): io.F[Nothing, Unit] = io.succeed(internal.write(methodName))
  }
  
  /** Consider not using this internal API. It can change in the future */
  object internal {
    private lazy val methodsRef: AtomicReference[List[String]] = new AtomicReference(Nil)

    def write(methodName: String): Unit = {
      methodsRef.getAndUpdate(methodName :: _)
      ()
    }

    def clear(): Unit =
      methodsRef.set(Nil)

    def calledMethods: List[String] = internal.methodsRef.get().reverse
  }
}

object CallLog {
  def apply(): CallLog = new CallLog()
  
  trait EffectfulAPI[F[_, _]] {
    def write(methodName: String): F[Nothing, Unit]
    
    def calledMethods: F[Nothing, Seq[String]]
  }
}
