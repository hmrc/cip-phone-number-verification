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

package uk.gov.hmrc.cipphonenumberverification.services

import play.api.Logging
import play.api.http.HttpEntity
import play.api.http.Status.NOT_FOUND
import play.api.libs.json.Json
import play.api.mvc.Results.{NotFound, Ok}
import play.api.mvc.{ResponseHeader, Result}
import uk.gov.hmrc.cipphonenumberverification.audit.{AuditType, VerificationDeliveryResultRequestAuditEvent}
import uk.gov.hmrc.cipphonenumberverification.connectors.GovUkConnector
import uk.gov.hmrc.cipphonenumberverification.models.ErrorResponse.Codes
import uk.gov.hmrc.cipphonenumberverification.models.govnotify.response.GovUkNotificationStatusResponse
import uk.gov.hmrc.cipphonenumberverification.models.{ErrorResponse, NotificationStatus}
import uk.gov.hmrc.cipphonenumberverification.utils.GovNotifyUtils
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class NotificationService @Inject()(govNotifyUtils: GovNotifyUtils, auditService: AuditService, govUkConnector: GovUkConnector)
                                   (implicit val executionContext: ExecutionContext) extends Logging {

  private val NO_DATA_FOUND = "No_data_found"

  def status(notificationId: String)(implicit hc: HeaderCarrier): Future[Result] = {
    def success(response: HttpResponse) = {
      val govNotifyResponse: GovUkNotificationStatusResponse = response.json.as[GovUkNotificationStatusResponse]
      val phoneNumber = govNotifyResponse.phone_number
      val passcode = govNotifyUtils.extractPasscodeFromGovNotifyBody(govNotifyResponse.body)
      val deliveryStatus = govNotifyResponse.status
      val (code, message) = deliveryStatus match {
        case "created" => (101, "Message is in the process of being sent")
        case "sending" => (102, "Message has been sent")
        case "pending" => (103, "Message is in the process of being delivered")
        case "sent" => (104, "Message was sent successfully")
        case "delivered" => (105, "Message was delivered successfully")
        case "permanent-failure" => (106, "Message was unable to be delivered by the network provider")
        case "temporary-failure" => (107, "Message was unable to be delivered by the network provider")
        case "technical-failure" => (108, "There is a problem with the notification vendor")
      }
      auditService.sendExplicitAuditEvent(AuditType.PHONE_NUMBER_VERIFICATION_DELIVERY_RESULT_REQUEST.toString,
        VerificationDeliveryResultRequestAuditEvent(phoneNumber, passcode, notificationId, deliveryStatus))
      Ok(Json.toJson(NotificationStatus(code, message)))
    }

    def failure(err: UpstreamErrorResponse) = {
      err.statusCode match {
        case NOT_FOUND =>
          logger.warn("Notification ID not found")
          auditService.sendExplicitAuditEvent(AuditType.PHONE_NUMBER_VERIFICATION_DELIVERY_RESULT_REQUEST.toString,
            VerificationDeliveryResultRequestAuditEvent(NO_DATA_FOUND, NO_DATA_FOUND, notificationId, NO_DATA_FOUND))
          NotFound(Json.toJson(ErrorResponse(Codes.NOT_FOUND, NO_DATA_FOUND)))
        case _ =>
          //TODO: Do we need a separate ticket to handle other errors?
          logger.error(err.message)
          Result.apply(ResponseHeader(err.statusCode), HttpEntity.NoEntity)
      }
    }

    govUkConnector.notificationStatus(notificationId).map {
      case Right(response) => success(response)
      case Left(err) => failure(err)
    }
  }
}
