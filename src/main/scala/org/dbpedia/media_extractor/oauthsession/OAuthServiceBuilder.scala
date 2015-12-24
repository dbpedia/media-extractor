package org.dbpedia.media_extractor.oauthsession

import com.github.scribejava.core.oauth.OAuthService

abstract class OAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String) {

  val providerName: String
  val oAuthService: OAuthService

  private val emptyToken = null

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