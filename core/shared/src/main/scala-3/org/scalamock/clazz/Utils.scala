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

import scala.annotation.{experimental, tailrec}
import scala.quoted.*

private[scalamock] class Utils(using val quotes: Quotes):
  def newApi = false

  import quotes.reflect.*

  case class StubWithMethod(stub: Term, method: MockableDefinition):
    def selectReflect[T: Type](name: MockableDefinition => String): Expr[T] =
      '{
        ${ stub.asExpr }
          .asInstanceOf[scala.reflect.Selectable]
          // https://github.com/lampepfl/dotty/issues/18612
          .selectDynamic(${ Expr(scala.reflect.NameTransformer.encode(name(method))) })
          .asInstanceOf[T]
      }

  object MockableDefinitions:
    @experimental
    def find(tpe: TypeRepr, name: String, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      def appliedTypesMatch(method: MockableDefinition, appliedTypes: List[TypeRepr]): Boolean =
        method.tpe match
          case poly: PolyType => poly.paramTypes.lengthCompare(appliedTypes) == 0
          case _ => appliedTypes.isEmpty

      def matchTypeConstructor(actual: TypeRepr, expected: TypeRepr): Boolean = {        
        // Extract type constructor symbols
        val actualSym = actual.typeSymbol
        val expectedSym = expected.typeSymbol
        // Check if they're the same type constructor or if actual is a subtype
        actualSym == expectedSym || {
          // Check if actual's type constructor conforms to expected
          actual.baseType(expectedSym) != NoPrefix
        }
      }

      def matchTypes(actualTypes: List[TypeRepr], expectedTypes: List[TypeRepr]): Boolean = {        
        if (actualTypes.length != expectedTypes.length) return false
        actualTypes.zip(expectedTypes).forall(matchType)
      }
      
      def matchType(actual: TypeRepr, expected: TypeRepr): Boolean =
        if (actual <:< expected) return true

        expected match {
          // Handle type parameters like F[_]
          case AppliedType(expectedTyCon, expectedArgs) =>
            actual match {
              case AppliedType(actualTyCon, actualArgs) =>
                // Check if type constructors match
                val tyConMatch = actualTyCon.typeSymbol == expectedTyCon.typeSymbol ||
                              actualTyCon <:< expectedTyCon ||
                              matchTypeConstructor(actualTyCon, expectedTyCon)
                
                if (!tyConMatch) return false
                
                // Check if args match (handling wildcards)
                if (expectedArgs.length != actualArgs.length) return false
                
                expectedArgs.zip(actualArgs).forall { case (expArg, actArg) =>
                  expArg match {
                    // Wildcard bounds like _ >: Nothing <: Any
                    case TypeBounds(lo, hi) =>
                      // Any concrete type fits within Nothing <: _ <: Any
                      actArg match {
                        case TypeBounds(actLo, actHi) =>
                          // Both are bounds, check compatibility
                          lo <:< actLo && actHi <:< hi
                        case _ =>
                          // Concrete type must fit within bounds
                          lo <:< actArg && actArg <:< hi
                      }
                    case _ =>
                      // Non-wildcard argument must match
                      matchType(actArg, expArg)
                  }
                }
                
              case _ => false
            }
          
          // Handle type parameters themselves (like F, Container, etc.)
          case param: TypeRepr if param.typeSymbol.isTypeParam =>
            // Type parameter without application - check if actual has same shape
            actual match {
              case AppliedType(_, _) => true // Any applied type matches F[_]
              case _ => actual.typeSymbol.isTypeParam
            }
          
          case _ => false
        }

      def typesMatch(method: MockableDefinition, paramTypes: List[TypeRepr]): Boolean =
        paramTypes.lengthCompare(method.parameterTypes) == 0 &&
          paramTypes.zip(method.parameterTypes).forall(matchType)

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

  extension (tpe: TypeRepr)
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
    val (rawTypes, rawResType) = tpe.widen.collectTypes
    @experimental val parameterTypes = prepareTypesFor(ownerTpe.typeSymbol).map(_.tpe).init

    @experimental
    private def thisTypeOverride(where: TypeRepr, classSymbol: Symbol): TypeRepr =
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
    def tpeOverride(classSymbol: Symbol): TypeRepr =
      innerOverride(thisTypeOverride(tpe, classSymbol), rawResType :: rawTypes, classSymbol)

    private def innerOverride(where: TypeRepr, types: List[TypeRepr], classSymbol: Symbol): TypeRepr =
      def collectInnerTypes(tpe: TypeRepr, ownerSymbol: Symbol): List[TypeRepr] =
        def loop(currentTpe: TypeRepr, names: List[String]): List[TypeRepr] =
          currentTpe match
            case AppliedType(inner, appliedTypes) => loop(inner, names) ++ appliedTypes.flatMap(collectInnerTypes(_, ownerSymbol))
            case TypeRef(inner, name) if name == ownerSymbol.name && names.nonEmpty => List(tpe)
            case TypeRef(inner, name) => loop(inner, name :: names)
            case _ => Nil
        loop(tpe, Nil)

      val pathDependentTypes = types.flatMap(collectInnerTypes(_, ownerTpe.typeSymbol))
      val pdUpdated = pathDependentTypes.map(innerTypeOverride(_, ownerTpe.typeSymbol, classSymbol, applyTypes = false))
      where.substituteTypes(pathDependentTypes.map(_.typeSymbol), pdUpdated)

    private def innerTypeOverride(tpe: TypeRepr, ownerSymbol: Symbol, newOwnerSymbol: Symbol, applyTypes: Boolean): TypeRepr =
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

    @experimental
    def prepareResType(classSymbol: Symbol, methodArgs: List[List[Tree]]): TypeRepr = {
      val resType =
        innerOverride(thisTypeOverride(rawResType, classSymbol), List(rawResType), classSymbol)

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

              case ff@TypeRef(ref@ParamRef(bindings, idx), name) =>
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
    }

    @experimental
    def prepareTypesFor(classSymbol: Symbol): List[TypeTree] = (rawTypes :+ thisTypeOverride(rawResType, classSymbol))
      .map(innerTypeOverride(_, ownerTpe.typeSymbol, classSymbol, applyTypes = true))
      .map(tpe => adjustTpe(tpe).asType match { case '[t] => TypeTree.of[t] })

    private def adjustTpe(tpe: TypeRepr): TypeRepr =
      def mapParamRefWithWildcard(tpe: TypeRepr): TypeRepr =
        tpe match
          case ParamRef(PolyType(_, bounds, _), idx) =>
            bounds(idx)
          case AppliedType(tycon, args) =>
            tycon.appliedTo(args.map(mapParamRefWithWildcard))
          case _ =>
            tpe

      @tailrec
      def resolveAndOrTypeParamRefs(tpe: TypeRepr): TypeRepr =
        tpe match {
          case AndType(left@(_: ParamRef | _: AppliedType), right@(_: ParamRef | _: AppliedType)) =>
            TypeRepr.of[Any]
          case AndType(left@(_: ParamRef | _: AppliedType), right) =>
            resolveAndOrTypeParamRefs(right)
          case AndType(left, right@(_: ParamRef | _: AppliedType)) =>
            resolveAndOrTypeParamRefs(left)
          case OrType(_: ParamRef | _: AppliedType, _) =>
            TypeRepr.of[Any]
          case OrType(_, _: ParamRef | _: AppliedType) =>
            TypeRepr.of[Any]
          case other =>
            other
        }


      resolveAndOrTypeParamRefs(mapParamRefWithWildcard(tpe.widen)) match
        case TypeBounds(lower, upper) => upper
        case AppliedType(TypeRef(_, "<repeated>"), elemTyps) =>
          TypeRepr.typeConstructorOf(classOf[Seq[?]]).appliedTo(elemTyps)
        case TypeRef(_: ParamRef, _) =>
          TypeRepr.of[Any]
        case AppliedType(TypeRef(_: ParamRef, _), _) =>
          TypeRepr.of[Any]
        case other =>
          other
    end adjustTpe
