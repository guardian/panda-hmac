package com.gu.pandahmac
import com.gu.hmac.HMACHeaders
import java.net.URI

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc._
import play.api.mvc.Results._
import com.gu.pandomainauth.model.{AuthenticatedUser, User}
import com.gu.pandomainauth.action.{AuthActions, UserRequest}
import javax.inject.Inject
import play.api.libs.ws.WSClient


object HMACHeaderNames {
  val hmacKey = "X-Gu-Tools-HMAC-Token"
  val dateKey = "X-Gu-Tools-HMAC-Date"
  // Optional header to give the emulated user a nice name, if this isn't present we default to 'hmac-authed-service'
  val serviceNameKey = "X-Gu-Tools-Service-Name"
}


trait HMACAuthActions extends AuthActions with HMACHeaders {
  /**
    * Play application
    * Play application components that you must provide in order to use AuthActions
    */
  def wsClient: WSClient
  def controllerComponents: ControllerComponents

  private implicit val ec: ExecutionContext = controllerComponents.executionContext

  private def authByKeyOrPanda[A](request: Request[A], block: RequestHandler[A], useApiAuth: Boolean): Future[Result] = {
    val oHmac: Option[String] = request.headers.get(HMACHeaderNames.hmacKey)
    val oDate: Option[String] = request.headers.get(HMACHeaderNames.dateKey)
    val oServiceName: Option[String] = request.headers.get(HMACHeaderNames.serviceNameKey)
    val uri = new URI(request.uri)

    (oHmac, oDate) match {
      case (Some(hmac), Some(date)) => {
        if (validateHMACHeaders(date, hmac, uri)) {
          val user = User(oServiceName.getOrElse("hmac-authed-service"), "", "", None)
          block(new UserRequest(user, request))
        } else {
          Future.successful(Unauthorized)
        }
      }
      case _ => if(useApiAuth) apiAuthByPanda(request, block) else authByPanda(request, block)
    }

  }

  type RequestHandler[A] = UserRequest[A] => Future[Result]

  def authByPanda[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
    AuthAction.invokeBlock(request, (request: UserRequest[A]) => {
      block(new UserRequest(request.user, request))
    })

  def apiAuthByPanda[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
    APIAuthAction.invokeBlock(request, (request: UserRequest[A]) => {
      block(new UserRequest(request.user, request))
    })


  /* as per https://www.playframework.com/documentation/2.6.x/ScalaActionsComposition */
  object HMACAuthAction extends ActionBuilder[UserRequest, AnyContent] {
    override def parser: BodyParser[AnyContent] = HMACAuthActions.this.controllerComponents.parsers.default
    override protected def executionContext: ExecutionContext = HMACAuthActions.this.controllerComponents.executionContext

    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] = {
      authByKeyOrPanda(request, block, useApiAuth = false)
    }
  }


  object APIHMACAuthAction extends ActionBuilder[UserRequest, AnyContent] {
    override def parser: BodyParser[AnyContent] = HMACAuthActions.this.controllerComponents.parsers.default
    override protected def executionContext: ExecutionContext = HMACAuthActions.this.controllerComponents.executionContext

    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
      authByKeyOrPanda(request, block, useApiAuth = true)
  }
}
