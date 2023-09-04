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

import com.codahale.metrics.Counter
import com.google.inject.Inject
import com.kenshoo.play.metrics.Metrics
import uk.gov.hmrc.cipphonenumberverification.models.response.VerificationStatus
import uk.gov.hmrc.http.UpstreamErrorResponse

import javax.inject.Singleton

@Singleton
class MetricsService @Inject() (metrics: Metrics) {

  private def recordMetric(metricName: String): Unit = getCounter(metricName).inc()

  def recordVerificationStatus(vs: VerificationStatus): Unit = getCounter(vs.status.toString).inc()

  def recordPhoneNumberValidated(telephoneNumberType: String): Unit = recordMetric(s"${telephoneNumberType.toLowerCase}_validation_count")

  def recordPhoneNumberNotValidated(): Unit = recordMetric("telephone_number_validation_failure")

  def recordMongoCacheFailure(): Unit = recordMetric("mongo_cache_failure")

  def recordUpstreamError(ue: UpstreamErrorResponse): Unit = recordMetric(s"upstream_error_response.${ue.statusCode}")

  def recordSendNotificationSuccess(): Unit = recordMetric("notification_success")

  def recordSendNotificationFailure(): Unit = recordMetric("notification_failure")

  def recordPasscodeVerified(): Unit = recordMetric("passcode_verification_success")

  def recordError(e: Throwable): Unit = recordMetric(e.getMessage)

  private def getCounter(counterName: String): Counter =
    metrics.defaultRegistry.counter(counterName)

}
