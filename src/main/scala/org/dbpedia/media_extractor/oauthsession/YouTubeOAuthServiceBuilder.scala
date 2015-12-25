package org.dbpedia.media_extractor.oauthsession

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.oauth.OAuthService
import com.github.scribejava.core.builder.ServiceBuilder

class YouTubeOAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String)
  extends {

    override val oAuthService: OAuthService = new ServiceBuilder()
      .provider(classOf[GoogleApi20])
      .apiKey(myApiKey)
      .apiSecret(myApiKeySecret)
      .scope("https://www.googleapis.com/auth/youtube.readonly")
      .callback("https://www.example.com/oauth2callback")
      .build()

  } with OAuthServiceBuilder(myApiKey, myApiKeySecret) {

  override val providerName = "YouTube"
}
