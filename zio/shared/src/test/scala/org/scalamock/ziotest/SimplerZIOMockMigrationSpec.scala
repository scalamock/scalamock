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

package org.scalamock.ziotest

import zio.test.{Spec, TestAspectAtLeastR, TestEnvironment, assertTrue}
import zio._

object SimplerZIOMockMigrationSpec extends ScalamockZIOSpec with SimplerZIOMockMigration {

  trait Service {
    def f(x: Int): UIO[String]
  }

  trait AnotherService {
    def g(x: String): UIO[Int]
  }

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("SimplerZIOMockMigration")(
      test("valid expectations should succeed") {
        val layer = createMockedLayer(
          ZIO.serviceWith[Service] { mock =>
            (mock.f _).expects(42).returnsZIO("success")
          }
        )

        val effect = for {
          value <- ZIO.serviceWithZIO[Service](_.f(42))
        } yield assertTrue(value == "success")

        effect.provideLayer(layer)
      },
      test("invalid expectations should fail") {
        val layer = createMockedLayer(
          ZIO.serviceWith[Service] { mock =>
            (mock.f _).expects(42).returnsZIO("expected")
          }
        )

        ZIO
          .serviceWithZIO[Service](_.f(41))
          .provideLayer(layer)
          .flip
          .catchSomeDefect { case e: org.scalamock.ziotest.ExpectationException =>
            ZIO.succeed(e.getMessage)
          }
          .map { errorMessage =>
            assertTrue(errorMessage.contains("UNSATISFIED"))
          }
      },
      test("two mocks should work together") {
        val layer1 = createMockedLayer(
          ZIO.serviceWith[Service] { mock =>
            (mock.f _).expects(1).returnsZIO("one")
          }
        )

        val layer2 = createMockedLayer(
          ZIO.serviceWith[AnotherService] { mock =>
            (mock.g _).expects("test").returnsZIO(42)
          }
        )

        val effect = for {
          service1 <- ZIO.service[Service]
          service2 <- ZIO.service[AnotherService]
          result1 <- service1.f(1)
          result2 <- service2.g("test")
        } yield assertTrue(result1 == "one" && result2 == 42)

        effect.provideLayer(layer1 ++ layer2)
      }
    )

  // have to override aspects, here verifyExpectations is not called
  // otherwise it's not possible to recover from the failure in the aspect, and succeed the test
  override def aspects: Chunk[TestAspectAtLeastR[TestEnvironment]] =
    scalamockInternalSpecAspects
}
