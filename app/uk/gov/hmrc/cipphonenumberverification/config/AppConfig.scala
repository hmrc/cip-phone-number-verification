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

package uk.gov.hmrc.cipphonenumberverification.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration) {

  lazy val appName: String = config.get[String]("appName")
  lazy val validateUrlProtocol: String = config.get[String]("microservice.services.cipphonenumber.validation.protocol")
  lazy val validateUrlHost: String = config.get[String]("microservice.services.cipphonenumber.validation.host")
  lazy val validateUrlPort: String = config.get[String]("microservice.services.cipphonenumber.validation.port")
  lazy val govUkNotifyHost: String = config.get[String]("microservice.services.govuk-notify.host")
  lazy val templateId: String = config.get[String]("microservice.services.govuk-notify.template_id")
  lazy val govUkNotifyApiKeyName: String =
    config.get[String]("microservice.services.govuk-notify.api-key.key-name")
  lazy val govUkNotifyApiKeyIssUuid: String =
    config.get[String]("microservice.services.govuk-notify.api-key.iss-uuid")
  lazy val govUkNotifyApiKeySecretKeyUuid: String =
    config.get[String]("microservice.services.govuk-notify.api-key.secret-key-uuid")
  lazy val cacheExpiry: Long = config.get[Long]("cache.expiry")
}
