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

package uk.gov.hmrc.cipphonenumberverification.models.api

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{maxLength, minLength}
import play.api.libs.json._

case class PhoneNumber(phoneNumber: String)

object PhoneNumber {

  object verification {
    implicit val reads: Reads[PhoneNumber] = Json.reads[PhoneNumber]
  }

  object validation {
    val MIN_LENGTH = 7
    val MAX_LENGTH = 20

    implicit val phoneNumberReads: Reads[PhoneNumber] =
      (JsPath \ "phoneNumber")
        .read[String](
          minLength[String](MIN_LENGTH)
            .keepAnd(maxLength[String](MAX_LENGTH))
        )
        .map(PhoneNumber.apply)
  }
}
