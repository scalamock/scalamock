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
    val callLog = c.Expr[Option[CallLog]](q"Some(_root_.org.scalamock.stubs.ZCallLog())")
    val maker = new StubMaker[c.type](c)
    val stub = new maker.StubMakerInner[T](createdStubs, stubUniqueIndexGenerator, callLog).make
    c.Expr[StubZIO[T]](q"new _root_.org.scalamock.stubs.StubZIO($stub)($tag)")
  }
}
