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

package org.scalamock.stubs.internal

import org.scalamock.context.MockContext
import org.scalamock.util.MacroAdapter.Context
import org.scalamock.stubs.{StubbedMethod, Stub, StubbedMethod0}

private[scalamock]
object StubMakerImpl {

  def stub[T: c.WeakTypeTag](c: Context)(
    createdStubs: c.Expr[CreatedStubs],
    stubUniqueIndexGenerator: c.Expr[StubUniqueIndexGenerator]
  ): c.Expr[Stub[T]] = {
    val maker = new StubMaker[c.type](c)
    new maker.StubMakerInner[T](createdStubs, stubUniqueIndexGenerator, maker.summonCallLog).make
  }

  def toStubbedMethod0[R: c.WeakTypeTag](c: Context)(f: c.Expr[R]): c.Expr[StubbedMethod0[R]] =
    StubbedMethodFinder.find[StubbedMethod0[R]](c)(f, List(c.weakTypeOf[Unit]))
  
  def toStubbedMethod1[T1: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[T1 => R]): c.Expr[StubbedMethod[T1, R]] =
    StubbedMethodFinder.find[StubbedMethod[T1, R]](c)(f, List(c.weakTypeOf[T1]))

  def toStubbedMethod2[T1: c.WeakTypeTag, T2: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2) => R]): c.Expr[StubbedMethod[(T1, T2), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2]))

  def toStubbedMethod3[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3) => R]): c.Expr[StubbedMethod[(T1, T2, T3), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3]))

  def toStubbedMethod4[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4]))

  def toStubbedMethod5[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5]))

  def toStubbedMethod6[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6]))

  def toStubbedMethod7[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7]))

  def toStubbedMethod8[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8]))

  def toStubbedMethod9[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9]))

  def toStubbedMethod10[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10]))

  def toStubbedMethod11[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11]))

  def toStubbedMethod12[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12]))

  def toStubbedMethod13[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13]))

  def toStubbedMethod14[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14]))

  def toStubbedMethod15[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15]))

  def toStubbedMethod16[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16]))

  def toStubbedMethod17[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17]))

  def toStubbedMethod18[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18]))

  def toStubbedMethod19[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19]))

  def toStubbedMethod20[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20]))

  def toStubbedMethod21[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, T21: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20], c.weakTypeOf[T21]))

  def toStubbedMethod22[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, T21: c.WeakTypeTag, T22: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R]): c.Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]] =
    StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20], c.weakTypeOf[T21], c.weakTypeOf[T22]))


}
