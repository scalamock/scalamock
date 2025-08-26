package org.scalamock.stubs.internal

import org.scalamock.stubs.{StubbedZIOMethod, StubbedMethod}
import org.scalamock.util.MacroAdapter.Context

private[scalamock]
object ZIOStubMakerImpl {
  def toStubbedMethod00[R: c.WeakTypeTag](c: Context)(f: c.Expr[R])(ev: c.Expr[Any]): c.Expr[StubbedZIOMethod[Unit, R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[Unit, R]](c)(f, List(c.weakTypeOf[Unit]))
    c.Expr[StubbedZIOMethod[Unit, R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }

  def toStubbedMethod0[R: c.WeakTypeTag](c: Context)(f: c.Expr[R]): c.Expr[StubbedZIOMethod[Unit, R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[Unit, R]](c)(f, List(c.weakTypeOf[Unit]))
    c.Expr[StubbedZIOMethod[Unit, R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }

  def toStubbedMethod1[T1: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[T1 => R]): c.Expr[StubbedZIOMethod[T1, R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[T1, R]](c)(f, List(c.weakTypeOf[T1]))
    c.Expr[StubbedZIOMethod[T1, R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }

  def toStubbedMethod2[T1: c.WeakTypeTag, T2: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2) => R]): c.Expr[StubbedZIOMethod[(T1, T2), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2]))
    c.Expr[StubbedZIOMethod[(T1, T2), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod3[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod4[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod5[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod6[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod7[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod8[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod9[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod10[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod11[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod12[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod13[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod14[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod15[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod16[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod17[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod18[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod19[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod20[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod21[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, T21: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20], c.weakTypeOf[T21]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  def toStubbedMethod22[T1: c.WeakTypeTag, T2: c.WeakTypeTag, T3: c.WeakTypeTag, T4: c.WeakTypeTag, T5: c.WeakTypeTag, T6: c.WeakTypeTag, T7: c.WeakTypeTag, T8: c.WeakTypeTag, T9: c.WeakTypeTag, T10: c.WeakTypeTag, T11: c.WeakTypeTag, T12: c.WeakTypeTag, T13: c.WeakTypeTag, T14: c.WeakTypeTag, T15: c.WeakTypeTag, T16: c.WeakTypeTag, T17: c.WeakTypeTag, T18: c.WeakTypeTag, T19: c.WeakTypeTag, T20: c.WeakTypeTag, T21: c.WeakTypeTag, T22: c.WeakTypeTag, R: c.WeakTypeTag](c: Context)(f: c.Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R]): c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]] = {
    import c.universe._
    val sm = StubbedMethodFinder.find[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]](c)(f, List(c.weakTypeOf[T1], c.weakTypeOf[T2], c.weakTypeOf[T3], c.weakTypeOf[T4], c.weakTypeOf[T5], c.weakTypeOf[T6], c.weakTypeOf[T7], c.weakTypeOf[T8], c.weakTypeOf[T9], c.weakTypeOf[T10], c.weakTypeOf[T11], c.weakTypeOf[T12], c.weakTypeOf[T13], c.weakTypeOf[T14], c.weakTypeOf[T15], c.weakTypeOf[T16], c.weakTypeOf[T17], c.weakTypeOf[T18], c.weakTypeOf[T19], c.weakTypeOf[T20], c.weakTypeOf[T21], c.weakTypeOf[T22]))
    c.Expr[StubbedZIOMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]](q"new _root_.org.scalamock.stubs.StubbedZIOMethod($sm)")
  }
  
  
}
