package com.gu.pandahmac

import com.amazonaws.handlers.RequestHandler
import com.gu.hmac.HMACHeaders

import java.net.URI

import scala.concurrent.Future

import play.api.http._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Security.AuthenticatedRequest

import com.gu.pandomainauth.model.{AuthenticatedUser, User}
import com.gu.pandomainauth.action.{AuthActions, UserRequest}


object HMACHeaderNames {
  val hmacKey = "X-Gu-Tools-HMAC-Token"
  val dateKey = "X-Gu-Tools-HMAC-Date"
  // Optional header to give the emulated user a nice name, if this isn't present we default to 'hmac-authed-service'
  val serviceNameKey = "X-Gu-Tools-Service-Name"
}


trait HMACAuthActions extends AuthActions with HMACHeaders {
  private def authByKeyOrPanda[A](request: Request[A], block: RequestHandler[A]): Future[Result] = {
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
      case _ => authByPanda(request, block)
    }

  }

  type RequestHandler[A] = UserRequest[A] => Future[Result]

  def authByPanda[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
    AuthAction.invokeBlock(request, (request: UserRequest[A]) => {
      block(new UserRequest(request.user, request))
    })

  object HMACAuthAction extends ActionBuilder[UserRequest] {
    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] = {
      // do hmac stuff then do panda stuff by calling AuthAction.invokeBlock
      authByKeyOrPanda(request, block)
    }
  }

  object APIHMACAuthAction extends ActionBuilder[UserRequest] {
    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
      authByKeyOrPanda(request, block)
  }
}
