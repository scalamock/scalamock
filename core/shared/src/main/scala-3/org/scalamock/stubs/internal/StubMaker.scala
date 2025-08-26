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

import org.scalamock.clazz.Utils
import org.scalamock.stubs.{CallLog, Stub, StubIO, StubbedMethod}

import scala.annotation.{experimental, tailrec}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.quoted.{Expr, Quotes, Type}

private[stubs] class StubMaker(
  using Quotes
) extends Utils:
  import quotes.reflect.*

  override def newApi = true

  @experimental
  def newInstance[T: Type](
    collector: Expr[CreatedStubs],
    stubUniqueIndexGenerator: Expr[StubUniqueIndexGenerator]
  ): Expr[Stub[T]] =
    val tpe = TypeRepr.of[T]
    val parents = parentsOf[T]
    val methods = MockableDefinitions(tpe)
    val log = Expr.summon[CallLog]

    val classSymbol = Symbol.newClass(
      parent = Symbol.spliceOwner,
      name = "anon",
      parents = parents.map {
        case term: Term => term.tpe
        case tpt: TypeTree => tpt.tpe
      },
      decls = classSymbol =>
        methods.flatMap { method =>
          List(
            Symbol.newVal(
              parent = classSymbol,
              name = method.stubValName,
              tpe = TypeRepr.of[StubbedMethod.Internal[Any, Any]],
              flags = Flags.EmptyFlags,
              privateWithin = Symbol.noSymbol
            ),
            if method.symbol.isValDef then
              Symbol.newVal(
                parent = classSymbol,
                name = method.symbol.name,
                tpe = method.tpeWithSubstitutedInnerTypesFor(classSymbol),
                flags = Flags.Override,
                privateWithin = Symbol.noSymbol
              )
            else
              Symbol.newMethod(
                parent = classSymbol,
                name = method.symbol.name,
                tpe = method.tpeWithSubstitutedInnerTypesFor(classSymbol),
                flags = Flags.Override,
                privateWithin = Symbol.noSymbol
              ),
          )
        } ::: List(
          Symbol.newMethod(
            parent = classSymbol,
            name = ClearStubsMethodName,
            tpe = MethodType(Nil)(_ => Nil, _ => TypeRepr.of[Unit]),
            flags = Flags.EmptyFlags,
            privateWithin = Symbol.noSymbol
          ),
          Symbol.newVal(
            parent = classSymbol,
            name = UniqueStubIdx,
            tpe = TypeRepr.of[Int],
            flags = Flags.EmptyFlags,
            privateWithin = Symbol.noSymbol
          )
        ),
      selfType = None
    )

    val classDef = ClassDef(
      cls = classSymbol,
      parents = parents,
      body = List(
        DefDef(
          symbol = classSymbol.methodMember(ClearStubsMethodName).head,
          _ =>
            Some(
              Block(
                methods.map { method => '{ ${ method.stubbedMethodRef(classSymbol) }.clear() }.asTerm },
                '{}.asTerm
              )
            )
        ),
        ValDef(
          symbol = classSymbol.declaredField(UniqueStubIdx),
          Some('{ $stubUniqueIndexGenerator.nextIdx() }.asTerm)
        )
      ) ::: methods.flatMap { method =>
        val resTypeWithOverride = method.resTypeWithInnerTypesOverrideFor(classSymbol)
        List(
          ValDef(
            classSymbol.declaredField(method.stubValName),
            Some {
              val callLog = Expr.summon[CallLog] match {
                case Some(log) => '{ Some(${log}) }
                case None => '{ None }
              }
              val idx = Ref(classSymbol.declaredField(UniqueStubIdx)).asExprOf[Int]
              val ioOpt = Expr.summon[StubIO].filter(_.isMatches(resTypeWithOverride)) match
                case Some(io) => '{ Some($io) }
                case None => '{None}

              '{
                new StubbedMethod.Internal[Any, Any](
                  s"<stub-" + $idx + "> " + ${ Expr(method.show) },
                  $callLog,
                  $ioOpt
                )
              }.asTerm
            }
          ),
          if (method.symbol.isValDef)
            ValDef(method.symbol.overridingSymbol(classSymbol), Some('{ null }.asTerm))
          else
            DefDef(
              symbol = method.symbol.overridingSymbol(classSymbol),
              params => Some {
                val resTpe = method.tpe.prepareResType(resTypeWithOverride, params)
                resTpe.asType match
                  case '[res] =>
                    val tupledArgs = tupled(params.flatMap {_.collect { case term: Term => term }}).asExpr
                    '{ ${ method.stubbedMethodRef(classSymbol) }.impl(${tupledArgs}).asInstanceOf[res] }
                      .asTerm
                      .changeOwner(method.symbol.overridingSymbol(classSymbol))
              }
            )
        )
      }
    )

    val instance = Block(
      List(classDef),
      Typed(
        Apply(
          Select(New(TypeIdent(classSymbol)), classSymbol.primaryConstructor),
          Nil
        ),
        TypeTree.of[T & scala.reflect.Selectable]
      )
    )
    //println(instance.show)
    '{
      ${ collector }.bind(${ instance.asExprOf[T] }.asInstanceOf[Stub[T]])
    }
  end newInstance

  @tailrec
  private def tupleTypeToList(
    tpe: TypeRepr,
    acc: mutable.ListBuffer[TypeRepr] = new ListBuffer[TypeRepr]
  ): List[TypeRepr] =
    tpe.asType match
      case '[h *: t] =>
        tupleTypeToList(TypeRepr.of[t], acc += TypeRepr.of[h])
      case '[EmptyTuple] =>
        acc.toList
      case _ =>
        (acc += tpe).toList


  @experimental
  def getStubbed00[R: Type](select: Expr[R]): Expr[StubbedMethod[Unit, R]] =
    searchTermWithMethod(select.asTerm, Nil).selectReflect[StubbedMethod[Unit, R]](_.stubValName)

  @experimental
  def getStubbed0[R: Type](select: Expr[() => R]): Expr[StubbedMethod[Unit, R]] =
    searchTermWithMethod(select.asTerm, Nil).selectReflect[StubbedMethod[Unit, R]](_.stubValName)

  @experimental
  def getStubbed[Args: Type, R: Type](select: Expr[Any]): Expr[StubbedMethod[Args, R]] =
    searchTermWithMethod(select.asTerm, tupleTypeToList(TypeRepr.of[Args])).selectReflect[StubbedMethod[Args, R]](_.stubValName)

  private def tupled(args: List[Term]): Term =
    args match
      case Nil => '{()}.asTerm
      case oneArg :: Nil => oneArg
      case args =>
        args.foldRight[Term]('{ EmptyTuple }.asTerm) { (el, acc) =>
          Select
            .unique(acc, "*:")
            .appliedToTypes(List(el.tpe, acc.tpe))
            .appliedToArgs(List(el))
        }


  extension (method: MockableDefinition)
    private def stubbedMethodRef(classSymbol: Symbol): Expr[StubbedMethod.Internal[Any, Any]] =
      Ref(classSymbol.declaredField(method.stubValName)).asExprOf[StubbedMethod.Internal[Any, Any]]

    private def show: String =
      given Printer[TypeRepr] = Printer.TypeReprShortCode
      s"${method.ownerTpe.show}.${method.symbol.name}${method.tpe.show}"


  extension (io: Expr[StubIO])
    private def isMatches(resTpe: TypeRepr): Boolean =
      resTpe <:< AppliedType(
        io.asTerm.tpe.select(io.asTerm.tpe.typeSymbol.typeMember("F")),
        List(TypeRepr.of[Any], TypeRepr.of[Any])
      )
