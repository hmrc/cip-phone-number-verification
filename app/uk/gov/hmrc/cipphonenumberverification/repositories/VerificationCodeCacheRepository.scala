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

package uk.gov.hmrc.cipphonenumberverification.repositories

import uk.gov.hmrc.cipphonenumberverification.config.AppConfig
import uk.gov.hmrc.cipphonenumberverification.models.internal.PhoneNumberVerificationCodeData
import uk.gov.hmrc.cipphonenumberverification.models.request.PhoneNumberAndVerificationCode
import uk.gov.hmrc.mongo.cache.{CacheIdType, DataKey, MongoCacheRepository}
import uk.gov.hmrc.mongo.{MongoComponent, TimestampSupport}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

class VerificationCodeCacheRepository @Inject() (mongoComponent: MongoComponent, config: AppConfig, timestampSupport: TimestampSupport)(implicit
  ec: ExecutionContext
) extends MongoCacheRepository(mongoComponent = mongoComponent,
                               collectionName = config.appName,
                               ttl = config.cacheExpiry.minutes,
                               timestampSupport = timestampSupport,
                               cacheIdType = CacheIdType.SimpleCacheId
    )

object VerificationCodeCacheRepository {
  val phoneNumberVerificationCodeDataDataKey: DataKey[PhoneNumberVerificationCodeData] = DataKey("phone-number-verification")
  val phoneNumberVerificationCodeDataKey: DataKey[PhoneNumberAndVerificationCode]      = DataKey("phone-number-verification")
}
