package org.scalamock.stubs.internal

import org.scalamock.stubs._
import org.scalamock.util.MacroAdapter.Context
import zio.Tag

private[scalamock]
object StubZIOMacroImpl {
  def stubZIO[T: c.WeakTypeTag](c: Context)(
    createdStubs: c.Expr[CreatedStubs],
    stubUniqueIndexGenerator: c.Expr[StubUniqueIndexGenerator],
    tag: c.Expr[Tag[Stub[T]]]
  ): c.Expr[StubZIO[T]] = {
    import c.universe._
    val stub = StubMakerImpl.stub[T](c)(createdStubs, stubUniqueIndexGenerator)
    c.Expr[StubZIO[T]](q"new _root_.org.scalamock.stubs.StubZIO($stub)($tag)")
  }
}
