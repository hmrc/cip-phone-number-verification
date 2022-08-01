/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.cipphonenumberverification

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcCurlRequestLogger

class VerifyIntegrationSpec extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"

  "/verify" should {
    "return 200 with valid telephone number" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            """{"phoneNumber": "07811123456"}""".stripMargin
          })
          .futureValue

      response.status shouldBe 202
    }

    //TODO: Fix as part of CAV-242
    "respond with 400 status for invalid request" ignore {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/verify")
          .withRequestFilter(AhcCurlRequestLogger())
          .post(Json.parse {
            s"""{"phoneNumber": ""}""".stripMargin
          })
          .futureValue

      response.status shouldBe 400
      (response.json \ "code").as[String] shouldBe "VALIDATION_ERROR"
      (response.json \ "message").as[String] shouldBe "Enter a valid phone number"
    }
  }
}