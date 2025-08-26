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

import scala.language.implicitConversions
import scala.util.NotGiven
import scala.concurrent.Future

private[scalamock]
trait StubsBase {

  /** Collects all generated stubs */
  final given stubs: internal.CreatedStubs = internal.CreatedStubs()
  
  /** Generates unique index for each stub */
  final given internal.StubUniqueIndexGenerator = internal.StubUniqueIndexGenerator()

  /**
   *  Resets all recorded stub functions and arguments.
   *  This is useful if you want to create your stub once per suite.
   *  Note that in such case test cases should run sequentially
   */
  final def resetStubs(): Unit = stubs.clearAll()

  /** Generates an object of type T with possibility to record methods arguments and set-up method results */
  inline def stub[T]: Stub[T] = stubImpl[T]
}

trait Stubs extends StubsBase {

  implicit inline def stubbed[R](inline f: => R)(using R <:< Future[?]): StubbedMethod[Unit, R] =
    stubbed00Impl[R](f)

  implicit inline def stubbed[R](inline f: () => R)(using R =:= R): StubbedMethod[Unit, R] =
    stubbed0Impl[R](f)

  implicit inline def stubbed[T1, R](inline f: T1 => R)(using (T1, R) =:= (T1, R)): StubbedMethod[T1, R] =
    stubbed1Impl[T1, R](f)

  implicit inline def stubbed[T1, T2, R](inline f: (T1, T2) => R)(using (T1, T2, R) =:= (T1, T2, R)): StubbedMethod[(T1, T2), R] =
    stubbed2Impl[T1, T2, R](f)
    
  implicit inline def stubbed[T1, T2, T3, R](inline f: (T1, T2, T3) => R)(using (T1, T2, T3, R) =:= (T1, T2, T3, R)): StubbedMethod[(T1, T2, T3), R] =
    stubbed3Impl[T1, T2, T3, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, R](inline f: (T1, T2, T3, T4) => R)(using (T1, T2, T3, T4, R) =:= (T1, T2, T3, T4, R)): StubbedMethod[(T1, T2, T3, T4), R] =
    stubbed4Impl[T1, T2, T3, T4, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, R](inline f: (T1, T2, T3, T4, T5) => R)(using (T1, T2, T3, T4, T5, R) =:= (T1, T2, T3, T4, T5, R)): StubbedMethod[(T1, T2, T3, T4, T5), R] =
    stubbed5Impl[T1, T2, T3, T4, T5, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, R](inline f: (T1, T2, T3, T4, T5, T6) => R)(using (T1, T2, T3, T4, T5, T6, R) =:= (T1, T2, T3, T4, T5, T6, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6), R] =
    stubbed6Impl[T1, T2, T3, T4, T5, T6, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, R](inline f: (T1, T2, T3, T4, T5, T6, T7) => R)(using (T1, T2, T3, T4, T5, T6, T7, R) =:= (T1, T2, T3, T4, T5, T6, T7, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
    stubbed7Impl[T1, T2, T3, T4, T5, T6, T7, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
    stubbed8Impl[T1, T2, T3, T4, T5, T6, T7, T8, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
    stubbed9Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
    stubbed10Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
    stubbed11Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
    stubbed12Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
    stubbed13Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
    stubbed14Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
    stubbed15Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
    stubbed16Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
    stubbed17Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
    stubbed18Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
    stubbed19Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
    stubbed20Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
    stubbed21Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](f)
    
  implicit inline def stubbed[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R)(using (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R) =:= (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R)): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
    stubbed22Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](f)
}
