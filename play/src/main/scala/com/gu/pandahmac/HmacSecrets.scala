package com.gu.pandahmac

import com.gu.hmac.ValidateHMACHeader

import java.net.URI

trait HMACSecrets extends ValidateHMACHeader {
  @deprecated("In an upcoming release consumers will expected to provide a list of secrets to `secretKeys` to allow for secret rotation.")
  def secret: String = ""
  def secretKeys: List[String] = List.empty

  protected def gatherSecrets = {
    val gatheredSecrets = (secret, secretKeys) match {
      case ("", emptyList) if emptyList.isEmpty => List.empty
      case (nonEmptySecret, _) if !nonEmptySecret.isEmpty => List(nonEmptySecret)
      case ("", nonEmptyList) => nonEmptyList
    }

    if (gatheredSecrets.isEmpty) {
      throw new Exception("Please set either 'secret' or 'secretKeys', no secret available for HMACAuthActions!")
    }

    gatheredSecrets
  }

  def validateHMACHeaders(date: String, hmac: String, uri: URI): Boolean =
    gatherSecrets.exists(validateHMACHeadersWithSecret(_, date, hmac, uri))
}