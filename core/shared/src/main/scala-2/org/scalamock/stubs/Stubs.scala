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

import org.scalamock.stubs.internal.StubMakerImpl

import scala.language.experimental.macros
import scala.language.implicitConversions

private[scalamock]
trait StubsBase {
  final implicit val createdStubs: internal.CreatedStubs = new internal.CreatedStubs()
  final implicit val stubUniqueIndexGenerator: internal.StubUniqueIndexGenerator = new internal.StubUniqueIndexGenerator()

  /** Resets all recorded stub functions and arguments.
   *  This is useful if you want to create your stub once per suite.
   *  Note that in such case test cases should run sequentially
   */
  final def resetStubs(): Unit = createdStubs.clearAll()

  /** Generates an object of type T with possibility to record methods arguments and set-up method results */
  def stub[T](implicit
    createdStubs: internal.CreatedStubs,
    stubUniqueIndexGenerator: internal.StubUniqueIndexGenerator
  ): Stub[T] = macro StubMakerImpl.stub[T]
}

trait Stubs extends StubsBase {
  implicit def stubbed[R](f: () => R): StubbedMethod[Unit, R] =
    macro StubMakerImpl.toStubbedMethod0[R]

  implicit def stubbed[T1, R](f: T1 => R): StubbedMethod[T1, R] =
    macro StubMakerImpl.toStubbedMethod1[T1, R]

  implicit def stubbed[T1, T2, R](f: (T1, T2) => R): StubbedMethod[(T1, T2), R] =
    macro StubMakerImpl.toStubbedMethod2[T1, T2, R]

  implicit def stubbed[T1, T2, T3, R](f: (T1, T2, T3) => R): StubbedMethod[(T1, T2, T3), R] =
    macro StubMakerImpl.toStubbedMethod3[T1, T2, T3, R]

  implicit def stubbed[T1, T2, T3, T4, R](f: (T1, T2, T3, T4) => R): StubbedMethod[(T1, T2, T3, T4), R] =
    macro StubMakerImpl.toStubbedMethod4[T1, T2, T3, T4, R]

  implicit def stubbed[T1, T2, T3, T4, T5, R](f: (T1, T2, T3, T4, T5) => R): StubbedMethod[(T1, T2, T3, T4, T5), R] =
    macro StubMakerImpl.toStubbedMethod5[T1, T2, T3, T4, T5, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, R](f: (T1, T2, T3, T4, T5, T6) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6), R] =
    macro StubMakerImpl.toStubbedMethod6[T1, T2, T3, T4, T5, T6, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, R](f: (T1, T2, T3, T4, T5, T6, T7) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    macro StubMakerImpl.toStubbedMethod7[T1, T2, T3, T4, T5, T6, T7, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    macro StubMakerImpl.toStubbedMethod8[T1, T2, T3, T4, T5, T6, T7, T8, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    macro StubMakerImpl.toStubbedMethod9[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    macro StubMakerImpl.toStubbedMethod10[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    macro StubMakerImpl.toStubbedMethod11[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    macro StubMakerImpl.toStubbedMethod12[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    macro StubMakerImpl.toStubbedMethod13[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    macro StubMakerImpl.toStubbedMethod14[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    macro StubMakerImpl.toStubbedMethod15[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    macro StubMakerImpl.toStubbedMethod16[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    macro StubMakerImpl.toStubbedMethod17[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    macro StubMakerImpl.toStubbedMethod18[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    macro StubMakerImpl.toStubbedMethod19[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    macro StubMakerImpl.toStubbedMethod20[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    macro StubMakerImpl.toStubbedMethod21[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R]

  implicit def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    macro StubMakerImpl.toStubbedMethod22[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R]
}


