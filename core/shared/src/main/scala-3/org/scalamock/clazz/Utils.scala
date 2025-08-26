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

package org.scalamock.clazz

import org.scalamock.stubs.Stub
import org.scalamock.util.Defaultable

import scala.annotation.{experimental, tailrec}
import scala.quoted.*
private[scalamock] class Utils(using val quotes: Quotes):
  def newApi = false

  import quotes.reflect.*

  def parentsOf[T: Type]: List[TypeTree | Term] =
    val tpe = TypeRepr.of[T]
    val isTrait = tpe.dealias.typeSymbol.flags.is(Flags.Trait)
    val isJavaClass = tpe.classSymbol.exists(sym => sym.flags.is(Flags.JavaDefined) && !sym.flags.is(Flags.Trait) && !sym.flags.is(Flags.Abstract))

    if (isJavaClass)
      report.errorAndAbort("Can't mock a java class due to https://github.com/lampepfl/dotty/issues/18694. Extend it manually with scala and then mock")

    def asParent(tree: TypeTree): TypeTree | Term =
      val constructorTypes = tree.tpe.dealias.typeSymbol.primaryConstructor
        .paramSymss.flatten.filter(_.isType).map(_.name)

      val constructorFields = tree.tpe.dealias.typeSymbol.primaryConstructor
        .paramSymss.filterNot(_.exists(_.isType))

      if tree.tpe.typeArgs.length < constructorTypes.length then
        report.errorAndAbort("Not all types are applied")

      if constructorFields.forall(_.isEmpty) then
        tree
      else
        val con = Select(
          New(TypeIdent(tree.tpe.typeSymbol)),
          tree.tpe.typeSymbol.primaryConstructor
        ).appliedToTypes(tree.tpe.typeArgs)

        val typeNames = {
          @tailrec
          def collectTypes(tpe: TypeRepr, acc: Map[String, TypeRepr]): Map[String, TypeRepr] =
            tpe match
              case MethodType(names, types, res) => collectTypes(res, acc ++ names.zip(types))
              case tpe => acc

          collectTypes(con.tpe.widenTermRefByName, Map.empty).view.mapValues {
            case n: ByNameType => n.widenByName
            case n => n
          }.toMap
        }

        con.appliedToArgss(
          constructorFields
            .map(_.map { sym => typeNames(sym.name).asType })
            .map(_.map { case '[t] => '{ ${Expr.summon[Defaultable[t]].get}.default }.asTerm })
        )
    end asParent

    if isTrait then
      List(TypeTree.of[Object], asParent(TypeTree.of[T]), TypeTree.of[scala.reflect.Selectable])
    else
      List(asParent(TypeTree.of[T]), TypeTree.of[scala.reflect.Selectable])


  case class StubWithMethod(stub: Term, method: MockableDefinition):
    def selectReflect[T: Type](name: MockableDefinition => String): Expr[T] =
      '{
        ${ stub.asExpr }
          .asInstanceOf[scala.reflect.Selectable]
          // https://github.com/lampepfl/dotty/issues/18612
          .selectDynamic(${ Expr(scala.reflect.NameTransformer.encode(name(method))) })
          .asInstanceOf[T]
      }


  @experimental
  def searchTermWithMethod(term: Term, argTypes: List[TypeRepr], appliedTypes: List[TypeTree] = Nil): StubWithMethod =
    term match
      case Select(mock, methodName) =>
        val tpe =
          mock.tpe.widenTermRefByName match
            case AppliedType(tycon, List(typ)) if newApi && tycon =:= TypeRepr.of[Stub] =>
              typ
            case tpe if newApi =>
              report.errorAndAbort(s"This syntax is only available on Stub[T]")
            case other =>
              other

        val method = MockableDefinitions.find(tpe, methodName, argTypes, appliedTypes.map(_.tpe))
        StubWithMethod(mock, method)

      case Block(_, Inlined(_, _, Inlined(_, _, term: Select))) =>
        searchTermWithMethod(term, argTypes)

      case Inlined(_, _, term) =>
        searchTermWithMethod(term, argTypes)

      case Block(stats@List(_: ValDef), term) =>  // var m = mock[T]
        val StubWithMethod(underlying, method) = searchTermWithMethod(term, argTypes)
        StubWithMethod(Block(stats, underlying), method)

      case Block(List(DefDef(_, _, _, Some(term))), _) =>
        searchTermWithMethod(term, argTypes)

      case Typed(term, teps) =>
        searchTermWithMethod(term, argTypes)

      case Lambda(_, term) =>
        searchTermWithMethod(term, argTypes)

      case Apply(term, _) =>
        searchTermWithMethod(term, argTypes)

      case TypeApply(term, types) =>
        searchTermWithMethod(term, argTypes, types)

      case Ident(fun) if !newApi =>
        report.errorAndAbort(
          s"please declare '$fun' as MockFunctionX or StubFunctionX (e.g val $fun: MockFunction1[X, R] = ... if it has 1 parameter)"
        )
      case _ =>
        report.errorAndAbort(
          s"""
              This syntax is only available on Stub[T].
              Current structure is not recognised: ${term.show}
              If you think this is a bug - please open a ticket at https://github.com/ScalaMock/ScalaMock/issues"
          """
        )

  extension (tpe: TypeRepr)
    def collectInnerTypes(ownerSymbol: Symbol): List[TypeRepr] =
      def loop(currentTpe: TypeRepr, names: List[String]): List[TypeRepr] =
        currentTpe match
          case AppliedType(inner, appliedTypes) => loop(inner, names) ++ appliedTypes.flatMap(_.collectInnerTypes(ownerSymbol))
          case TypeRef(inner, name) if name == ownerSymbol.name && names.nonEmpty => List(tpe)
          case TypeRef(inner, name) => loop(inner, name :: names)
          case _ => Nil

      loop(tpe, Nil)

    def innerTypeOverride(ownerSymbol: Symbol, newOwnerSymbol: Symbol, applyTypes: Boolean): TypeRepr =
      @tailrec
      def loop(currentTpe: TypeRepr, names: List[(String, List[TypeRepr])], appliedTypes: List[TypeRepr]): TypeRepr =
        currentTpe match
          case AppliedType(inner, appliedTypes) =>
            loop(inner, names, appliedTypes)

          case TypeRef(inner, name) if name == ownerSymbol.name && names.nonEmpty =>
            names.foldLeft[TypeRepr](This(newOwnerSymbol).tpe) { case (tpe, (name, appliedTypes)) =>
              tpe
                .select(tpe.typeSymbol.typeMember(name))
                .appliedTo(appliedTypes.filter(_ => applyTypes))
            }

          case TypeRef(inner, name) =>
            loop(inner, name -> appliedTypes :: names, Nil)

          case other =>
            tpe

      if (ownerSymbol == newOwnerSymbol)
        tpe
      else
        loop(tpe, Nil, Nil)


    def mapParamRefWithWildcard: TypeRepr =
      tpe match
        case ParamRef(PolyType(_, bounds, _), idx) =>
          bounds(idx)
        case AppliedType(tycon, args) =>
          tycon.appliedTo(args.map(_.mapParamRefWithWildcard))
        case _ =>
          tpe

    def resolveAndOrTypeParamRefs: TypeRepr =
      tpe match {
        case AndType(left @ (_: ParamRef | _: AppliedType), right @ (_: ParamRef | _: AppliedType)) =>
          TypeRepr.of[Any]
        case AndType(left @ (_: ParamRef | _: AppliedType), right) =>
          right.resolveAndOrTypeParamRefs
        case AndType(left, right @ (_: ParamRef | _: AppliedType)) =>
          left.resolveAndOrTypeParamRefs
        case OrType(_: ParamRef | _: AppliedType, _) =>
          TypeRepr.of[Any]
        case OrType(_, _: ParamRef | _: AppliedType) =>
          TypeRepr.of[Any]
        case other =>
          other
      }

    @experimental
    def prepareResType(resType: TypeRepr, methodArgs: List[List[Tree]]) =
      tpe match
        case baseBindings: PolyType =>
          def loop(typeRepr: TypeRepr): TypeRepr =
            typeRepr match
              case pr@ParamRef(bindings, idx) if bindings == baseBindings =>
                methodArgs.head(idx).asInstanceOf[TypeTree].tpe

              case AndType(left, right) =>
                AndType(loop(left), loop(right))

              case OrType(left, right) =>
                OrType(loop(left), loop(right))

              case AppliedType(tycon, args) =>
                AppliedType(loop(tycon), args.map(arg => loop(arg)))

              case ff @ TypeRef(ref @ ParamRef(bindings, idx), name) =>
                def getIndex(bindings: TypeRepr): Int =
                  @tailrec
                  def loop(bindings: TypeRepr, idx: Int): Int =
                    bindings match
                      case MethodType(_, _, method: MethodType) => loop(method, idx + 1)
                      case _ => idx

                  loop(bindings, 1)

                val maxIndex = methodArgs.length
                val parameterListIdx = maxIndex - getIndex(bindings)

                TypeSelect(methodArgs(parameterListIdx)(idx).asInstanceOf[Term], name).tpe

              case other => other

          loop(resType)
        case _ =>
          resType


    def collectTypes: (List[TypeRepr], TypeRepr) =
      @tailrec
      def loop(currentTpe: TypeRepr, argTypesAcc: List[List[TypeRepr]], resType: TypeRepr): (List[TypeRepr], TypeRepr) =
        currentTpe match
          case PolyType(_, _, res)          => loop(res, List.empty[TypeRepr] :: argTypesAcc, resType)
          case MethodType(_, argTypes, res) => loop(res, argTypes :: argTypesAcc, resType)
          case other                        => (argTypesAcc.reverse.flatten, other)
      loop(tpe, Nil, TypeRepr.of[Nothing])

  case class MockableDefinition(idx: Int, symbol: Symbol, ownerTpe: TypeRepr):
    val mockValName = s"mock$$${symbol.name}$$$idx"
    val stubValName = s"stub$$${symbol.name}$$$idx"
    val tpe = ownerTpe.memberType(symbol)
    private val (rawTypes, rawResType) = tpe.widen.collectTypes
    @experimental val parameterTypes = prepareTypesFor(ownerTpe.typeSymbol).map(_.tpe).init

    @experimental
    private def overrideThisType(where: TypeRepr, classSymbol: Symbol): TypeRepr =
      symbol.info match
        case tpe: LambdaType =>
          tpe.resType match
            case tpe: ThisType =>
              where.substituteTypes(List(tpe.typeSymbol), List(This(classSymbol).tpe))
            case _ =>
              where
        case _ =>
          where

    @experimental
    def resTypeWithInnerTypesOverrideFor(classSymbol: Symbol): TypeRepr =
      updatePathDependent(overrideThisType(rawResType, classSymbol), List(rawResType), classSymbol)

    @experimental
    def tpeWithSubstitutedInnerTypesFor(classSymbol: Symbol): TypeRepr =
      updatePathDependent(overrideThisType(tpe, classSymbol), rawResType :: rawTypes, classSymbol)

    private def updatePathDependent(where: TypeRepr, types: List[TypeRepr], classSymbol: Symbol): TypeRepr =
      val pathDependentTypes = types.flatMap(_.collectInnerTypes(ownerTpe.typeSymbol))
      val pdUpdated = pathDependentTypes.map(_.innerTypeOverride(ownerTpe.typeSymbol, classSymbol, applyTypes = false))
      where.substituteTypes(pathDependentTypes.map(_.typeSymbol), pdUpdated)

    @experimental
    def prepareTypesFor(classSymbol: Symbol) = (rawTypes :+ overrideThisType(rawResType, classSymbol))
      .map(_.innerTypeOverride(ownerTpe.typeSymbol, classSymbol, applyTypes = true))
      .map { typeRepr =>
        val adjusted =
          typeRepr.widen.mapParamRefWithWildcard.resolveAndOrTypeParamRefs match
            case TypeBounds(lower, upper) => upper
            case AppliedType(TypeRef(_, "<repeated>"), elemTyps) =>
              TypeRepr.typeConstructorOf(classOf[Seq[?]]).appliedTo(elemTyps)
            case TypeRef(_: ParamRef, _) =>
              TypeRepr.of[Any]
            case AppliedType(TypeRef(_: ParamRef, _), _) =>
              TypeRepr.of[Any]
            case other =>
              other
        adjusted.asType match
          case '[t] => TypeTree.of[t]
      }

  object MockableDefinitions:
    @experimental
    def find(tpe: TypeRepr, name: String, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      def appliedTypesMatch(method: MockableDefinition, appliedTypes: List[TypeRepr]): Boolean =
        method.tpe match
          case poly: PolyType => poly.paramTypes.lengthCompare(appliedTypes) == 0
          case _ => appliedTypes.isEmpty

      def typesMatch(method: MockableDefinition, paramTypes: List[TypeRepr]): Boolean =
        paramTypes.lengthCompare(method.parameterTypes) == 0 &&
          paramTypes.zip(method.parameterTypes).forall(_ <:< _)

      MockableDefinitions(tpe)
        .filter(m => m.symbol.name == name && typesMatch(m, paramTypes) && appliedTypesMatch(m, appliedTypes))
        .sortWith((a, b) => a.parameterTypes.zip(b.parameterTypes).forall(_ <:< _))
        .headOption
        .getOrElse(report.errorAndAbort(s"Method with such signature not found"))


    def apply(tpe: TypeRepr): List[MockableDefinition] =
      val methods = (tpe.typeSymbol.methodMembers.toSet -- TypeRepr.of[Object].typeSymbol.methodMembers).toList
        .filter(sym =>
          !sym.flags.is(Flags.Private) &&
            !sym.flags.is(Flags.Final) &&
            !sym.flags.is(Flags.Mutable) &&
            !sym.flags.is(Flags.Artifact) &&
            sym.privateWithin.isEmpty &&
            !sym.name.contains("$default$")
        )
        .zipWithIndex
        .map((sym, idx) => MockableDefinition(idx, sym, tpe))

      val vals = tpe.typeSymbol.fieldMembers
        .filter(_.flags.is(Flags.Deferred))
        .map(sym => MockableDefinition(0, sym, tpe))
      methods ++ vals
