package org.dbpedia.media_extractor.oauthsession

import org.scribe.oauth.OAuthService
import org.scribe.model.Token

abstract class OAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String) {
  val providerName: String
  val oAuthService: OAuthService

  val emptyToken = null

  val requestToken = try {
    oAuthService.getRequestToken()
  } catch {
    // OAuth2
    case _: UnsupportedOperationException => emptyToken
  }

  def getAuthorizationUrl(): String = {
    oAuthService.getAuthorizationUrl(requestToken)
  }

}