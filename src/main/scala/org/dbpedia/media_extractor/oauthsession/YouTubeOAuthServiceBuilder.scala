package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.Google2Api
import org.scribe.oauth.OAuthService

class YouTubeOAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String)
  extends {

    override val oAuthService: OAuthService = new ServiceBuilder()
      .provider(classOf[Google2Api])
      .apiKey(myApiKey)
      .apiSecret(myApiKeySecret)
      .scope("https://www.googleapis.com/auth/youtube.readonly")
      .callback("https://www.example.com/oauth2callback")
      .build()

  } with OAuthServiceBuilder(myApiKey, myApiKeySecret) {

  override val providerName = "YouTube"
}
