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
  val dateKey  = "X-Gu-Tools-HMAC-Date"
}


trait HMACAuthActions extends AuthActions {
  val secret: String

  //private def authByKey[A](request: Request[A], block: RequestHandler[A]): Future[Result] = {
  //  val hmac: Option[String] = request.headers.get(HMACHeaderNames.hmacKey)
  //  val date: Option[String] = request.headers.get(HMACHeaderNames.dateKey)
  //  val uri = new URI(request.uri)

  //  //hmacService.validateHMACHeaders(date, token, uri)

  //}
  type RequestHandler[A] = UserRequest[A] => Future[Result]

  def authByPanda[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
    AuthAction.invokeBlock(request, (request: UserRequest[A]) => {
      block(new UserRequest(request.user, request))
    })

  object HMACAuthAction extends ActionBuilder[UserRequest] {
    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] = {
      // do hmac stuff then do panda stuff by calling AuthAction.invokeBlock
      authByPanda(request, block)
    }
  }

  object APIHMACAuthAction extends ActionBuilder[UserRequest] {
    override def invokeBlock[A](request: Request[A], block: RequestHandler[A]): Future[Result] =
      authByPanda(request, block)
    }
}
