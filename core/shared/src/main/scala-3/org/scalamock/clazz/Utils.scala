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

    def find(tpe: TypeRepr, selected: Symbol, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      def member =
        tpe.memberType(selected).widen match
          case signature @ (_: MethodType | _: PolyType) => s"${selected.name}${signature.show}"
          case other => s"${selected.name}: ${other.show}"
      def isReceiverMember =
        tpe.typeSymbol.methodMembers.contains(selected) || tpe.typeSymbol.fieldMembers.contains(selected)
      MockableDefinitions(tpe).find(_.symbol == selected) match
        case Some(definition) if matchesSignature(definition, paramTypes, appliedTypes) =>
          definition
        case Some(_) =>
          report.errorAndAbort(
            s"Selected member $member of ${tpe.show} does not match the function's parameter types " +
              paramTypes.map(_.show).mkString("(", ", ", ")")
          )
        case None if isReceiverMember =>
          val reason = exclusionReason(selected).fold("")(reason => s": $reason")
          report.errorAndAbort(s"Selected member $member of ${tpe.show} is unavailable for mocking$reason")
        case None =>
          // The receiver's class doesn't list the selected symbol, as when the receiver is typed through an
          // opaque alias's bound and the class implements it with another member. Reflection can't ask the
          // compiler which member that is, so this case keeps the lookup by name and argument types that
          // predates symbol selection, including its known weakness of binding a related overload.
          findByArgumentTypes(tpe, selected.name, paramTypes, appliedTypes)

    private def findByArgumentTypes(tpe: TypeRepr, name: String, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): MockableDefinition =
      inTraversalOrder(tpe)
        .filter(method => method.symbol.name == name && matchesSignature(method, paramTypes, appliedTypes))
        .sortWith((a, b) => a.rawTypes.zip(b.rawTypes).forall(_ <:< _))
        .headOption
        .getOrElse(report.errorAndAbort(s"Method with such signature not found"))

    private def matchesSignature(method: MockableDefinition, paramTypes: List[TypeRepr], appliedTypes: List[TypeRepr]): Boolean =
      // should have same parameter count
      paramTypes.lengthCompare(method.rawTypes) == 0 &&
        // only poly should have appliedTypes
        (method.tpe match {
          case poly: PolyType => poly.paramTypes.lengthCompare(appliedTypes.length) == 0
          case _ => appliedTypes.isEmpty
        }) &&
        // params with filled ParamRef and few fixes should match
        paramTypes.zip(method.rawTypes.map(adjustTpe(_, method.tpe, appliedTypes))).forall(_ <:< _)

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

    private def exclusionReason(sym: Symbol): Option[String] =
      if objectMethods.contains(sym) then Some("it is a java.lang.Object method")
      else if sym.flags.is(Flags.Private) then Some("it is private")
      else if sym.flags.is(Flags.Final) then Some("it is final")
      else if sym.flags.is(Flags.Mutable) then Some("it is mutable")
      else if sym.flags.is(Flags.Artifact) then Some("it is compiler-generated")
      else if sym.privateWithin.nonEmpty then Some("it has qualified private or protected visibility")
      else if sym.name.contains("$default$") then Some("it is a default argument getter")
      else if sym.hasAnnotation(TypeRepr.of[scala.deprecatedOverriding].typeSymbol) then
        Some("it is annotated with @deprecatedOverriding")
      else if sym.isValDef && !sym.flags.is(Flags.Deferred) then Some("it is a concrete val")
      else None

    private def inTraversalOrder(tpe: TypeRepr): List[MockableDefinition] =
      val methods = tpe.typeSymbol.methodMembers.filter(exclusionReason(_).isEmpty)

      val vals = tpe.typeSymbol.fieldMembers.filter(_.flags.is(Flags.Deferred))
      val members = methods ++ vals

      // Within each name, number the members that take parameters by erased signature. A parameterless
      // member is named without an index (see MockableDefinition), so it doesn't shift them. Global
      // traversal follows compiler name hashes, which differ between runs and must not determine a mock
      // class's or expectation's field names.
      def erasedSignature(sym: Symbol): String =
        val signature = sym.signature
        signature.paramSigs.mkString(",") + ")" + signature.resultSig
      val memberIndexInName: Map[Symbol, Int] =
        members.filterNot(isParameterless).groupBy(_.name).values
          .flatMap(_.sortBy(erasedSignature).zipWithIndex)
          .toMap

      members.map(sym => MockableDefinition(memberIndexInName.getOrElse(sym, ParameterlessIdx), sym, tpe))

    def apply(tpe: TypeRepr): List[MockableDefinition] =
      val definitions = inTraversalOrder(tpe)
      // Native JS overload dispatch currently depends on the final property initializer. Preserve its
      // traversal until that separate dispatch bug is fixed; ordinary Scala types use one stable list
      // for both class generation and expectation lookup.
      if isJsAny(tpe) then definitions else definitions.sortBy(d => (d.symbol.name, d.idx))

  extension (tpe: TypeRepr)
    def collectTypes: (List[TypeRepr], TypeRepr) =
      @tailrec
      def loop(currentTpe: TypeRepr, argTypesAcc: List[List[TypeRepr]], resType: TypeRepr): (List[TypeRepr], TypeRepr) =
        currentTpe match
          case PolyType(_, _, res)          => loop(res, List.empty[TypeRepr] :: argTypesAcc, resType)
          case MethodType(_, argTypes, res) => loop(res, argTypes :: argTypesAcc, resType)
          case other                        => (argTypesAcc.reverse.flatten, other)
      loop(tpe, Nil, TypeRepr.of[Nothing])

  // A class has at most one parameterless member per name (val x, def x or def x()), and overriding keeps
  // it parameterless. Naming its field without an index keeps that name the same through every parent,
  // whether the parent declares a val, a def, or only same-name methods that take parameters.
  private def isParameterless(sym: Symbol): Boolean = sym.signature.paramSigs.isEmpty

  // Sorts a parameterless member before same-name members that take parameters.
  private val ParameterlessIdx = -1

  case class MockableDefinition(idx: Int, symbol: Symbol, ownerTpe: TypeRepr):
    private val fieldKey = if isParameterless(symbol) then "nullary" else idx.toString
    val mockValName = s"mock$$${symbol.name}$$$fieldKey"
    val stubValName = s"stub$$${symbol.name}$$$fieldKey"
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
