// Copyright (c) 2011-2015 ScalaMock Contributors (https://github.com/paulbutcher/ScalaMock/graphs/contributors)
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

import org.scalamock.util.MacroAdapter.Context
import org.scalamock.util.{MacroAdapter, MacroUtils}
import org.scalamock.stubs.StubbedMethod
import org.scalamock.stubs.{CallLog, StubIO, Stub}

private[scalamock]
class StubMaker[C <: Context](val ctx: C) {
  class StubMakerInner[T: ctx.WeakTypeTag](
    createdStubs: ctx.Expr[CreatedStubs],
    stubUniqueIndexGenerator: ctx.Expr[StubUniqueIndexGenerator]
  ) {
    import ctx.universe._

    import scala.language.reflectiveCalls

    val utils = new MacroUtils[ctx.type](ctx)

    import utils._
    import Flag._
    import definitions._

    def isPathDependentThis(t: Type): Boolean = t match {
      case TypeRef(pre, _, _) => isPathDependentThis(pre)
      case ThisType(tpe) => tpe == typeToMock.typeSymbol
      case _ => false
    }

    /**
      * Translates forwarder parameters into Trees.
      * Also maps Java repeated params into Scala repeated params
      */
    def forwarderParamType(t: Type): Tree = t match {
      case TypeRef(pre, sym, args) if sym == JavaRepeatedParamClass =>
        TypeTree(internalTypeRef(pre, RepeatedParamClass, args))
      case TypeRef(pre, sym, args) if isPathDependentThis(t) =>
        AppliedTypeTree(Ident(TypeName(sym.name.toString)), args map TypeTree _)
      case _ =>
        TypeTree(t)
    }

    /**
     *  Translates mock function parameters into Trees.
     *  The difference between forwarderParamType is that:
     *  T* and T... are translated into Seq[T]
     *
     *  see issue #24
     */
    def mockParamType(t: Type): Tree = t match {
      case TypeRef(pre, sym, args) if sym == JavaRepeatedParamClass || sym == RepeatedParamClass =>
        AppliedTypeTree(Ident(typeOf[Seq[_]].typeSymbol), args map TypeTree _)
      case TypeRef(pre, sym, args) if isPathDependentThis(t) =>
        AppliedTypeTree(Ident(TypeName(sym.name.toString)), args map TypeTree _)
      case _ =>
        TypeTree(t)
    }

    def methodsNotInObject =
      typeToMock.members filter (m => m.isMethod && !isMemberOfObject(m)) map (_.asMethod)

    //! TODO - This is a hack, but it's unclear what it should be instead. See
    //! https://groups.google.com/d/topic/scala-user/n11V6_zI5go/discussion
    def resolvedType(m: Symbol): Type =
      m.typeSignatureIn(internalSuperType(internalThisType(typeToMock.typeSymbol), typeToMock))

    def buildForwarderParams(methodType: Type) =
      paramss(methodType) map { params =>
        params map { p =>
          ValDef(
            Modifiers(PARAM | (if (p.isImplicit) IMPLICIT else NoFlags)),
            TermName(p.name.toString),
            forwarderParamType(p.typeSignature),
            EmptyTree)
        }
      }

    // def <|name|>(p1: T1, p2: T2, ...): T = <|mockname|>(p1, p2, ...)
    def methodDef(m: MethodSymbol, methodType: Type, body: Tree): DefDef = {
      val params = buildForwarderParams(methodType)
      val resType = forwarderParamType(finalResultType(methodType))

      DefDef(
        Modifiers(OVERRIDE),
        m.name,
        m.typeParams map { p => internalTypeDef(p) },
        params,
        resType,
        body
      )
    }

    def methodImpl(m: MethodSymbol, methodType: Type, body: Tree): DefDef = {
      methodType match {
        case NullaryMethodType(_) => methodDef(m, methodType, body)
        case MethodType(_, _) => methodDef(m, methodType, body)
        case PolyType(_, _) => methodDef(m, methodType, body)
        case _ => ctx.abort(ctx.enclosingPosition,
          s"ScalaMock: Don't know how to handle ${methodType.getClass}. Please open a ticket at https://github.com/paulbutcher/ScalaMock/issues")
      }
    }

    def forwarderImpl(m: MethodSymbol): ValOrDefDef = {
      val mt = resolvedType(m)
      if (m.isStable) {
        ValDef(
          Modifiers(),
          TermName(m.name.toString),
          TypeTree(mt),
          castTo(literal(null), mt))
      } else {
        val args = paramss(mt).flatten map { p => Ident(TermName(p.name.toString)) }
        val body = applyListOn(
          Select(This(anon), mockFunctionName(m)),
          "impl",
          List(tupledArgs(args))
        )
        methodImpl(m, mt, body)
      }
    }

    def mockFunctionName(m: MethodSymbol) = {
      val method = typeToMock.member(m.name).asTerm
      val index = method.alternatives.indexOf(m)
      assert(index >= 0)
      TermName("stub$" + m.name + "$" + index)
    }

    def tupledArgs(args: List[Tree]): Tree =
      args match {
        case Nil => Literal(Constant(()))
        case List(arg0) => arg0
        case List(arg0, arg1) => q"($arg0, $arg1)"
        case List(arg0, arg1, arg2) => q"($arg0, $arg1, $arg2)"
        case List(arg0, arg1, arg2, arg3) => q"($arg0, $arg1, $arg2, $arg3)"
        case List(arg0, arg1, arg2, arg3, arg4) => q"($arg0, $arg1, $arg2, $arg3, $arg4)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19, $arg20)"
        case List(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20, arg21) => q"($arg0, $arg1, $arg2, $arg3, $arg4, $arg5, $arg6, $arg7, $arg8, $arg9, $arg10, $arg11, $arg12, $arg13, $arg14, $arg15, $arg16, $arg17, $arg18, $arg19, $arg20, $arg21)"
        case _ => ctx.abort(ctx.enclosingPosition, "ScalaMock: Can't handle methods with more than 22 parameters (yet)")
      }

    def tupledType(args: List[Tree]): Tree = {
      args match {
        case Nil => TypeTree(typeOf[Unit])
        case head :: Nil => head
        case nonEmptyArgs =>
          val typeSymbolOfTuple = nonEmptyArgs.length match {
            case 2 => typeOf[Tuple2[_, _]].typeSymbol
            case 3 => typeOf[Tuple3[_, _, _]].typeSymbol
            case 4 => typeOf[Tuple4[_, _, _, _]].typeSymbol
            case 5 => typeOf[Tuple5[_, _, _, _, _]].typeSymbol
            case 6 => typeOf[Tuple6[_, _, _, _, _, _]].typeSymbol
            case 7 => typeOf[Tuple7[_, _, _, _, _, _, _]].typeSymbol
            case 8 => typeOf[Tuple8[_, _, _, _, _, _, _, _]].typeSymbol
            case 9 => typeOf[Tuple9[_, _, _, _, _, _, _, _, _]].typeSymbol
            case 10 => typeOf[Tuple10[_, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 11 => typeOf[Tuple11[_, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 12 => typeOf[Tuple12[_, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 13 => typeOf[Tuple13[_, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 14 => typeOf[Tuple14[_, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 15 => typeOf[Tuple15[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 16 => typeOf[Tuple16[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 17 => typeOf[Tuple17[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 18 => typeOf[Tuple18[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 19 => typeOf[Tuple19[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 20 => typeOf[Tuple20[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 21 => typeOf[Tuple21[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case 22 => typeOf[Tuple22[_, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _, _]].typeSymbol
            case _ =>
              ctx.abort(ctx.enclosingPosition, "ScalaMock: Can't handle methods with more than 22 parameters (yet)")
          }
          AppliedTypeTree(Ident(typeSymbolOfTuple), nonEmptyArgs)
      }
    }


    // val stub$<methodName>$<idx> = new StubApi.Internal[(T1, T2, ...), R]

    def mockMethod(m: MethodSymbol): ValDef = {
      val mt = resolvedType(m)
      val clazz = typeOf[StubbedMethod.Internal[_, _]]
      val finalRt = finalResultType(mt)
      val types = List(
        tupledType(paramTypes(mt).map(mockParamType)),
        mockParamType(finalRt)
      )
      val termName = mockFunctionName(m)
      val additionalAnnotations = if(isScalaJs) List(jsExport(termName.encodedName.toString)) else Nil
      val summonedLog = ctx.inferImplicitValue(typeOf[CallLog], silent = true)
      val summonedIO = ctx.inferImplicitValue(typeOf[StubIO], silent = true)
      val summonedIOOpt = (if (summonedIO != EmptyTree) Some(summonedIO) else None)
        .filter { io =>
          finalRt <:< appliedType(io.tpe.member(TypeName("F")), List(typeOf[Any], typeOf[Any]))
        }
      ValDef(
        Modifiers().mapAnnotations(additionalAnnotations ::: _),
        mockFunctionName(m),
        AppliedTypeTree(Ident(clazz.typeSymbol), types), // see issue #24
        callConstructor(
          New(AppliedTypeTree(Ident(clazz.typeSymbol), types)),
          generateMockMethodName(m, m.typeSignature),
          if (summonedLog == EmptyTree) q"None" else q"Some($summonedLog)",
          summonedIOOpt.fold(q"None": Tree)(io => q"Some($io)"),
        )
      )
    }

    def clearMethod(methods: List[MethodSymbol]): DefDef = {
      val termName = TermName("stubs$macro$clear")
      val additionalAnnotations = if(isScalaJs) List(jsExport(termName.encodedName.toString)) else Nil

      DefDef(
        Modifiers().mapAnnotations(additionalAnnotations ::: _),
        termName,
        Nil,
        List(List()),
        TypeTree(typeOf[Unit]),
        Block(
          methods.map(mockFunctionName(_))
            .map { name => Apply(Select(Select(This(anon), name), TermName("clear")), Nil) },
          q"()"
        )
      )
    }

    def mockIdxVal = {
      val termName = TermName("stubs$macro$idx")
      ValDef(
        Modifiers(),
        termName,
        TypeTree(typeOf[Int]),
        q"$stubUniqueIndexGenerator.nextIdx()"
      )
    }


    // def <init>() = super.<init>()
    def initDef = {
      val primaryConstructorOpt = typeToMock.members.collectFirst {
        case method: MethodSymbolApi if method.isPrimaryConstructor => method
      }

      val constructorArgumentsTypes = primaryConstructorOpt.map { constructor =>
      val constructorTypeContext = constructor.typeSignatureIn(typeToMock)
      val constructorArguments = constructorTypeContext.paramLists
      constructorArguments.map {
          symbols => symbols.map(_.typeSignatureIn(constructorTypeContext))
        }
      }

      val tnEmpty = TypeName("") // typeNames.EMPTY
      val tnConstructor = TermName("<init>") // termNames.CONSTRUCTOR
      val superCall: Tree = Select(Super(This(tnEmpty), tnEmpty), tnConstructor)
      val constructorCall = constructorArgumentsTypes.fold(Apply(superCall, Nil).asInstanceOf[Tree]) { symbols =>
        symbols.foldLeft(superCall) {
          case (acc, symbol) => Apply(acc, symbol.map(tpe => q"null.asInstanceOf[$tpe]"))
        }
      }

      DefDef(
        Modifiers(),
        tnConstructor,
        List(),
        List(List()),
        TypeTree(),
        Block(
          List(constructorCall),
          Literal(Constant(())))
      )
    }

    private def generateMockMethodName(method: MethodSymbol, methodType: Type): Tree = {
      val mockType: Type = typeToMock.resultType
      val mockTypeNamePart: String = mockType.typeSymbol.name.toString
      val mockTypeArgsPart: String = generateTypeArgsString(mockType.typeArgs)
      val idx: Tree = Select(This(anon), TermName("stubs$macro$idx"))

      val methodTypePart: String = methodType.toString
      val methodNamePart: String = method.name.toString

      // "<%s> %s%s.%s".format(objectNamePart, mockTypeNamePart, mockTypeArgsPart, methodNamePart, methodTypeParamsPart)
      val formatStr = applyOn(scalaPredef, "augmentString", literal("<stub-%s> %s%s.%s%s"))
      applyOn(formatStr, "format",
        idx, literal(mockTypeNamePart), literal(mockTypeArgsPart), literal(methodNamePart), literal(methodTypePart))
    }


    private def generateTypeArgsString(typeArgs: List[Any]): String = {
      if (typeArgs.nonEmpty)
        "[%s]".format(typeArgs.mkString(","))
      else ""
    }

    // new <|typeToMock|> { <|members|> }
    def anonClass(members: List[Tree]) =
      Block(
        List(
          ClassDef(
            Modifiers(FINAL),
            anon,
            List(),
            Template(
              List(TypeTree(typeToMock)),
              noSelfType,
              initDef +: members))),
        callConstructor(New(Ident(anon))))

    val typeToMock = weakTypeOf[T]
    val anon = TypeName("$anon")
    val methodsToMock = methodsNotInObject.filter { m =>
      !m.isConstructor && !m.isPrivate && m.privateWithin == NoSymbol &&
      !m.isFinal &&
        !m.asInstanceOf[reflect.internal.HasFlags].hasFlag(reflect.internal.Flags.BRIDGE) &&
        !m.isParamWithDefault && // see issue #43
        (!(m.isStable || m.isAccessor) ||
          m.asInstanceOf[reflect.internal.HasFlags].isDeferred) //! TODO - stop using internal if/when this gets into the API
    }.toList
    val forwarders = methodsToMock map forwarderImpl
    val mocks = methodsToMock.map(mockMethod(_))
    val members = clearMethod(methodsToMock) :: mockIdxVal :: (forwarders ++ mocks)

    def make: ctx.Expr[Stub[T]] = {
      val result = castTo(anonClass(members), typeToMock)

              //println("------------")
              //println(showRaw(result))
              //println("------------")
              //println(show(result))
              //println("------------")

      ctx.Expr[Stub[T]](q"$createdStubs.bind($result).asInstanceOf[_root_.org.scalamock.stubs.Stub[${weakTypeTag[T]}]]")
    }
  }
}
