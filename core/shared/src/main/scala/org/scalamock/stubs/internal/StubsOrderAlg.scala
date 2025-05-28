package org.scalamock.stubs.internal

trait StubsOrderAlg {
  protected def isBefore(calledMethods: Seq[String], first: String, second: String): Boolean =
    calledMethods.indexOf(second, calledMethods.indexOf(first)) != -1
    
  protected def isAfter(calledMethods: Seq[String], first: String, second: String): Boolean =
    calledMethods.indexOf(first, calledMethods.indexOf(second))!= -1
}
