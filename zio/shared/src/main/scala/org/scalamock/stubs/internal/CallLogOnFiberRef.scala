package org.scalamock.stubs.internal

import org.scalamock.stubs.CallLog
import zio._


class CallLogOnFiberRef(calledMethodsRef: FiberRef[Chunk[String]]) 
  extends CallLog.EffectfulAPI[IO] {

  override def calledMethods: UIO[Seq[String]] = calledMethodsRef.get

  override def write(methodName: String): UIO[Unit] = 
    calledMethodsRef.update(ch => ch.appended(methodName))
}

object CallLogOnFiberRef {
  private[scalamock] lazy val calledMethodsRef: FiberRef[Chunk[String]] = {
    val differ = Differ.chunk(Differ.update[String])
    Unsafe.unsafe(implicit unsafe =>
      FiberRef.unsafe.makePatch(Chunk.empty[String], differ, differ.empty)
    )
  }
  
  private[scalamock] def apply(): CallLog.EffectfulAPI[IO] = new CallLogOnFiberRef(calledMethodsRef)
}
