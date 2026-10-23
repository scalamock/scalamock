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

import org.scalamock.util.Defaultable

import scala.annotation.tailrec
import scala.quoted.*

private[scalamock] class Utils(using val quotes: Quotes):
  def newApi = false

  import quotes.reflect.*

  /**
   *  Scala.js native types can only be extended by a non-native JS class.
   *  Checked by name to stay safe on platforms without scalajs-library on the classpath.
   */
  def isJsAny(tpe: TypeRepr): Boolean =
    tpe.baseClasses.exists(_.fullName == "scala.scalajs.js.Any")

  private def jsMockRuntime: Symbol = Symbol.requiredModule("org.scalamock.util.JsMockRuntime")

  private def getJsMember[T: Type](target: Term, name: String): Expr[T] =
    val member = Apply(Select.unique(Ref(jsMockRuntime), "getMember"), List(target, Literal(StringConstant(name))))
    '{ ${ member.asExpr }.asInstanceOf[T] }

  private def setJsMember(target: Term, name: String, value: Term): Term =
    Apply(Select.unique(Ref(jsMockRuntime), "setMember"), List(target, Literal(StringConstant(name)), value))

  /** A non-native JS class is recognised by the Scala.js backend through this annotation. */
  def jsClassAnnotations(isJs: Boolean): List[Term] =
    if !isJs then Nil
    else
      val jsTypeAnnot = Symbol.classSymbol("scala.scalajs.js.annotation.internal.JSType")
      List(Apply(Select(New(TypeIdent(jsTypeAnnot)), jsTypeAnnot.primaryConstructor), Nil))

  /** Default value of each parameter that has one, in order; 'js.native' default args arrive as undefined. */
  private def jsDefaults(definition: MockableDefinition): Expr[Seq[Option[Any]]] =
    val paramSymbols = definition.symbol.paramSymss.flatten.filterNot(_.isType)
    Expr.ofSeq(
      paramSymbols.zip(definition.rawTypes).map { (paramSymbol, paramType) =>
        val default =
          if !paramSymbol.flags.is(Flags.HasDefault) then None
          else paramType.asType match
            case '[t] => Expr.summon[Defaultable[t]].map(d => '{ $d.default: Any })
        default.fold('{ None: Option[Any] })(value => '{ Some($value) })
      }
    )

  /**
   *  Members of a macro generated JS class are not exposed to JS by the compiler,
   *  so JS calls would reach the native member instead of the override.
   *  The mock function and a JS function forwarding to it are assigned as own properties,
   *  the former to be found by MockFunctionFinder, the latter to shadow the native member.
   */
  def jsExposedMembers(classSymbol: Symbol, definition: MockableDefinition, mockFunctionValDef: ValDef): List[Statement] =
    val mockFunctionRef = Select(This(classSymbol), mockFunctionValDef.symbol)
    val exposeMockFunction = setJsMember(This(classSymbol), definition.mockValName, mockFunctionRef)

    if definition.symbol.isValDef then List(exposeMockFunction)
    else
      val arity = definition.rawTypes.length
      val dispatcher = Apply(Select.unique(Ref(jsMockRuntime), s"fn$arity"), List(mockFunctionRef, jsDefaults(definition).asTerm))
      List(exposeMockFunction, setJsMember(This(classSymbol), definition.symbol.name, dispatcher))

  case class StubWithMethod(stub: Term, method: MockableDefinition):
    def selectReflect[T: Type](name: MockableDefinition => String): Expr[T] =
      if isJsAny(stub.tpe.widenTermRefByName) then
        getJsMember[T](stub, name(method))
      else
        '{
          ${ stub.asExpr }
            .asInstanceOf[scala.reflect.Selectable]
            // https://github.com/lampepfl/dotty/issues/18612
            .selectDynamic(${ Expr(scala.reflect.NameTransformer.encode(name(method))) })
            .asInstanceOf[T]
        }

  object MockableDefinitions:
    private val objectMethods = TypeRepr.of[Object].typeSymbol.methodMembers.toSet

    def find(tpe: TypeRepr, name: String, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      MockableDefinitions(tpe)
        .filter { method =>
          // should have same name
          method.symbol.name == name &&
          // should have same parameter count
          paramTypes.lengthCompare(method.rawTypes) == 0 &&
          // only poly should have appliedTypes
          (method.tpe match {
            case poly: PolyType => poly.paramTypes.lengthCompare(appliedTypes.length) == 0
            case _ => appliedTypes.isEmpty
          }) &&
            // params with filled ParamRef and few fixes should match
            paramTypes.zip(method.rawTypes.map(adjustTpe(_, method.tpe, appliedTypes))).forall(_ <:< _)
        }
        .sortWith((a, b) => a.rawTypes.zip(b.rawTypes).forall(_ <:< _))
        .headOption
        .getOrElse(report.errorAndAbort(s"Method with such signature not found"))

    private def adjustTpe(tpe: TypeRepr, methodTpe: TypeRepr, appliedTypes: List[TypeRepr]): TypeRepr =
      def updateParamRefs(tpe: TypeRepr, methodTpe: PolyType): TypeRepr =
        def replace(ref: ParamRef): TypeRepr =
          if appliedTypes.length > ref.paramNum then appliedTypes(ref.paramNum)
          else if methodTpe.paramBounds.length > ref.paramNum then methodTpe.paramBounds(ref.paramNum).hi
          else TypeRepr.of[Any]

        tpe match {
          case TypeBounds(lo, hi) =>
            TypeBounds(updateParamRefs(lo, methodTpe), updateParamRefs(hi, methodTpe))
            
          case p: ParamRef =>
            replace(p)

          case AppliedType(tycon: ParamRef, args) =>
            replace(tycon).appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case TypeRef(p: ParamRef, name) =>
            replace(p).typeSymbol.typeMember(name).typeRef

          case AppliedType(TypeRef(tycon: ParamRef, name), args) =>
            replace(tycon).typeSymbol.typeMember(name).typeRef.appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case AppliedType(tycon, args) =>
            tycon.appliedTo(args.map(updateParamRefs(_, methodTpe)))

          case AndType(left, right) =>
            AndType(updateParamRefs(left, methodTpe), updateParamRefs(right, methodTpe))

          case OrType(left, right) =>
            OrType(updateParamRefs(left, methodTpe), updateParamRefs(right, methodTpe))

          case FlexibleType(other) =>
            FlexibleType(updateParamRefs(other, methodTpe))

          case _ =>
            tpe
        }
      end updateParamRefs

      val preprocessed = tpe match
        case ByNameType(tpe) =>
          tpe
        case AppliedType(TypeRef(_, "<repeated>"), elemTyps) =>
          TypeRepr.typeConstructorOf(classOf[Seq[?]]).appliedTo(elemTyps)
        case tpe =>
          tpe

      methodTpe match
        case poly: PolyType =>
          updateParamRefs(preprocessed, poly)
        case _ =>
          preprocessed
    end adjustTpe

    def apply(tpe: TypeRepr): List[MockableDefinition] =
      val methods = tpe.typeSymbol.methodMembers
        .filter(sym =>
          !objectMethods.contains(sym) &&
            !sym.flags.is(Flags.Private) &&
            !sym.flags.is(Flags.Final) &&
            !sym.flags.is(Flags.Mutable) &&
            !sym.flags.is(Flags.Artifact) &&
            sym.privateWithin.isEmpty &&
            !sym.name.contains("$default$") &&
            !sym.hasAnnotation(TypeRepr.of[scala.deprecatedOverriding].typeSymbol)
        )

      // Number each method among its own overloads, ordered by erased signature, rather than by its
      // position among all of the type's members. That position follows the compiler's name hashes,
      // which differ between compiler runs, so a mock class and an expectation compiled in different
      // runs (incremental compilation, or a mock created in one module and used in another) would
      // otherwise look for different field names. Overloads of one name can't share an erased signature.
      def erasedSignature(sym: Symbol): String =
        val signature = sym.signature
        signature.paramSigs.mkString(",") + ")" + signature.resultSig
      val overloadIndex: Map[Symbol, Int] =
        methods.groupBy(_.name).values.flatMap(_.sortBy(erasedSignature).zipWithIndex).toMap

      val vals = tpe.typeSymbol.fieldMembers
        .filter(_.flags.is(Flags.Deferred))
        .map(sym => MockableDefinition(0, sym, tpe))
      methods.map(sym => MockableDefinition(overloadIndex(sym), sym, tpe)) ++ vals

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

    def prepareResType(classSymbol: Symbol, methodArgs: List[List[Tree]], debug: Boolean = false): TypeRepr = {
      val methodTpe = This(classSymbol).tpe.memberType(symbol)
      val resType = methodTpe.collectTypes._2 match
        case ByNameType(tpe) => tpe
        case other => other

      methodTpe match
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
                AppliedType(loop(tycon), args.map(loop))

              case AnnotatedType(other, annot) =>
                AnnotatedType(loop(other), annot)

              case FlexibleType(other) =>
                FlexibleType(loop(other))

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

        case baseBindings: MethodType =>
          resType match
            case pr@ParamRef(bindings, idx) if bindings == baseBindings =>
              methodArgs.head(idx).asInstanceOf[TypeTree].tpe
            case other =>
              other

        case _ =>
          resType
    }
