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

package uk.gov.hmrc.cipphonenumberverification.metrics

import com.codahale.metrics.Counter
import com.google.inject.Inject
import com.kenshoo.play.metrics.Metrics

import javax.inject.Singleton

@Singleton
class MetricsService @Inject() (metrics: Metrics) {

  def recordMetric(metricName: String) = getCounter(metricName).inc()

  private def getCounter(counterName: String): Counter =
    metrics.defaultRegistry.counter(counterName)

}
