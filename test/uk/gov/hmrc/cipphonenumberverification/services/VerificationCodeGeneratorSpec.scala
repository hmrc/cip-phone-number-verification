/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cipphonenumberverification.services

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class VerificationCodeGeneratorSpec extends AnyWordSpec with Matchers {

  private val generate = new VerificationCodeGenerator()

  "create 6 digit verification code" in {
    generate
      .generate()
      .forall(
        y => y.isUpper
      ) shouldBe true
    generate
      .generate()
      .forall(
        y => y.isLetter
      ) shouldBe true

    val illegalChars = List('@', '£', '$', '%', '^', '&', '*', '(', ')', '-', '+')
    generate.generate().toList map (
      y => assertResult(illegalChars contains y)(false)
    )

    generate.generate().length shouldBe 6
  }
}
