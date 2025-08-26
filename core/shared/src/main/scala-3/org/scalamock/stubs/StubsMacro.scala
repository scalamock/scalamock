package org.scalamock.stubs

import scala.annotation.experimental
import scala.quoted.{Expr, Quotes, Type}

private
inline def stubImpl[T](using 
  createdStubs: internal.CreatedStubs,
  stubUniqueIndexGenerator: internal.StubUniqueIndexGenerator
): Stub[T] =
  ${ stubMacro[T]('{ createdStubs }, '{ stubUniqueIndexGenerator }) }


private
inline def stubbed00Impl[R](inline f: R): StubbedMethod[Unit, R] =
  ${ stubbed00Macro[R]('{ f }) }

private
inline def stubbed0Impl[R](inline f: () => R): StubbedMethod[Unit, R] =
  ${ stubbed0Macro[R]('{ f }) }

private
inline def stubbed1Impl[T1, R](inline f: T1 => R): StubbedMethod[T1, R] =
  ${ stubbed1Macro[T1, R]('{ f }) }

private
inline def stubbed2Impl[T1, T2, R](inline f: (T1, T2) => R): StubbedMethod[(T1, T2), R] =
  ${ stubbed2Macro[T1, T2, R]('{ f }) }

private
inline def stubbed3Impl[T1, T2, T3, R](inline f: (T1, T2, T3) => R): StubbedMethod[(T1, T2, T3), R] =
  ${ stubbed3Macro[T1, T2, T3, R]('{ f }) }

private
inline def stubbed4Impl[T1, T2, T3, T4, R](inline f: (T1, T2, T3, T4) => R): StubbedMethod[(T1, T2, T3, T4), R] =
  ${ stubbed4Macro[T1, T2, T3, T4, R]('{ f }) }

private
inline def stubbed5Impl[T1, T2, T3, T4, T5, R](inline f: (T1, T2, T3, T4, T5) => R): StubbedMethod[(T1, T2, T3, T4, T5), R] =
  ${ stubbed5Macro[T1, T2, T3, T4, T5, R]('{ f }) }

private
inline def stubbed6Impl[T1, T2, T3, T4, T5, T6, R](inline f: (T1, T2, T3, T4, T5, T6) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6), R] =
  ${ stubbed6Macro[T1, T2, T3, T4, T5, T6, R]('{ f }) }

private
inline def stubbed7Impl[T1, T2, T3, T4, T5, T6, T7, R](inline f: (T1, T2, T3, T4, T5, T6, T7) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R] =
  ${ stubbed7Macro[T1, T2, T3, T4, T5, T6, T7, R]('{ f }) }

private
inline def stubbed8Impl[T1, T2, T3, T4, T5, T6, T7, T8, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R] =
  ${ stubbed8Macro[T1, T2, T3, T4, T5, T6, T7, T8, R]('{ f }) }

private
inline def stubbed9Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R] =
  ${ stubbed9Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, R]('{ f }) }

private
inline def stubbed10Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R] =
  ${ stubbed10Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R]('{ f }) }

private
inline def stubbed11Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R] =
  ${ stubbed11Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R]('{ f }) }

private
inline def stubbed12Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R] =
  ${ stubbed12Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R]('{ f }) }

private
inline def stubbed13Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R] =
  ${ stubbed13Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R]('{ f }) }

private
inline def stubbed14Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R] =
  ${ stubbed14Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R]('{ f }) }

private
inline def stubbed15Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R] =
  ${ stubbed15Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R]('{ f }) }
  
private 
inline def stubbed16Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R] =
  ${ stubbed16Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R]('{ f }) }
  
private 
inline def stubbed17Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R] =
  ${ stubbed17Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R]('{ f }) }
  
private 
inline def stubbed18Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R] =
  ${ stubbed18Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R]('{ f }) }
  
private 
inline def stubbed19Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R] =
  ${ stubbed19Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R]('{ f }) }
  
private 
inline def stubbed20Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R] =
  ${ stubbed20Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R]('{ f }) }

private 
inline def stubbed21Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R] =
  ${ stubbed21Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R]('{ f }) }
  
private 
inline def stubbed22Impl[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R](inline f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R): StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R] =
  ${ stubbed22Macro[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R]('{ f }) }

@experimental
private
def stubMacro[T: Type](
  collector: Expr[internal.CreatedStubs],
  stubUniqueIndexGenerator: Expr[internal.StubUniqueIndexGenerator]
)(using Quotes): Expr[Stub[T]] =
  new internal.StubMaker().newInstance[T](collector, stubUniqueIndexGenerator)


@experimental
private
def stubbed00Macro[R: Type](f: Expr[R])(using Quotes): Expr[StubbedMethod[Unit, R]] =
  new internal.StubMaker().getStubbed00[R](f)

@experimental
private
def stubbed0Macro[R: Type](f: Expr[() => R])(using Quotes): Expr[StubbedMethod[Unit, R]] =
  new internal.StubMaker().getStubbed0[R](f)

@experimental
private
def stubbed1Macro[T1: Type, R: Type](f: Expr[T1 => R])(using Quotes): Expr[StubbedMethod[T1, R]] =
  new internal.StubMaker().getStubbed[T1, R](f)

@experimental
private
def stubbed2Macro[T1: Type, T2: Type, R: Type](f: Expr[(T1, T2) => R])(using Quotes): Expr[StubbedMethod[(T1, T2), R]] =
  new internal.StubMaker().getStubbed[(T1, T2), R](f)

@experimental
private
def stubbed3Macro[T1: Type, T2: Type, T3: Type, R: Type](f: Expr[(T1, T2, T3) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3), R](f)

@experimental
private
def stubbed4Macro[T1: Type, T2: Type, T3: Type, T4: Type, R: Type](f: Expr[(T1, T2, T3, T4) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4), R](f)

@experimental
private
def stubbed5Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5), R](f)

@experimental
private
def stubbed6Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6), R](f)

@experimental
private
def stubbed7Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7), R](f)

@experimental
private
def stubbed8Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8), R](f)

@experimental
private
def stubbed9Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9), R](f)

@experimental
private
def stubbed10Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10), R](f)

@experimental
private
def stubbed11Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11), R](f)

@experimental
private
def stubbed12Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12), R](f)

@experimental
private
def stubbed13Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13), R](f)

@experimental
private
def stubbed14Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14), R](f)

@experimental
private
def stubbed15Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15), R](f)

@experimental
private
def stubbed16Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16), R](f)

@experimental
private
def stubbed17Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17), R](f)

@experimental
private
def stubbed18Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, T18: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18), R](f)

@experimental
private
def stubbed19Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, T18: Type, T19: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19), R](f)

@experimental
private
def stubbed20Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, T18: Type, T19: Type, T20: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20), R](f)

@experimental
private
def stubbed21Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, T18: Type, T19: Type, T20: Type, T21: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21), R](f)

@experimental
private
def stubbed22Macro[T1: Type, T2: Type, T3: Type, T4: Type, T5: Type, T6: Type, T7: Type, T8: Type, T9: Type, T10: Type, T11: Type, T12: Type, T13: Type, T14: Type, T15: Type, T16: Type, T17: Type, T18: Type, T19: Type, T20: Type, T21: Type, T22: Type, R: Type](f: Expr[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) => R])(using Quotes): Expr[StubbedMethod[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R]] =
  new internal.StubMaker().getStubbed[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22), R](f)




