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

package uk.gov.hmrc.cipphonenumberverification.controllers

import play.api.Logging
import play.api.libs.json._
import play.api.mvc.{Action, ControllerComponents, Request, Result}
import uk.gov.hmrc.cipphonenumberverification.metrics.MetricsService
import uk.gov.hmrc.cipphonenumberverification.models.api.ErrorResponse.Codes._
import uk.gov.hmrc.cipphonenumberverification.models.api.ErrorResponse.Message._
import uk.gov.hmrc.cipphonenumberverification.models.api.ErrorResponse._
import uk.gov.hmrc.cipphonenumberverification.models.api.PhoneNumber.validation._
import uk.gov.hmrc.cipphonenumberverification.models.api.{ErrorResponse, PhoneNumber, ValidatedPhoneNumber}
import uk.gov.hmrc.cipphonenumberverification.services.ValidateService
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class ValidateController @Inject() (cc: ControllerComponents, service: ValidateService, metricsService: MetricsService, auth: BackendAuthComponents)
    extends BackendController(cc)
    with Logging {

  val permission: Predicate.Permission = Predicate.Permission(Resource(ResourceType("cip-phone-number-validation"), ResourceLocation("*")), IAAction("*"))

  def validate: Action[JsValue] = auth.authorizedAction[Unit](permission).compose(Action(parse.json)).async {
    implicit request =>
      withJsonBody[PhoneNumber] {
        phoneNumber =>
          service.validate(phoneNumber.phoneNumber) match {
            case Left(errorResponse: ErrorResponse)                => Future.successful(BadRequest(Json.toJson(errorResponse)))
            case Right(validatedPhoneNumber: ValidatedPhoneNumber) => Future.successful(Ok(Json.toJson(validatedPhoneNumber)))
          }
      }
  }

  override protected def withJsonBody[T](f: T => Future[Result])(implicit request: Request[JsValue], m: Manifest[T], reads: Reads[T]): Future[Result] =
    request.body.validate[T] match {
      case JsSuccess(payload, _) => f(payload)
      case JsError(_) =>
        metricsService.recordMetric("telephone_number_validation_failure")
        logger.warn("Failed to validate request")
        Future.successful(BadRequest(Json.toJson(ErrorResponse(VALIDATION_ERROR.id, INVALID_TELEPHONE_NUMBER))))
    }
}