package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.FlickrApi
import org.scribe.oauth.OAuthService
import org.scribe.builder.ServiceBuilder
import org.scribe.model.Token
import org.scribe.builder.api.Google2Api

class YouTubeOAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String)
  extends OAuthServiceBuilder(myApiKey, myApiKeySecret) {

  override val providerName = "YouTube"

  override val oAuthService: OAuthService = new ServiceBuilder()
    .provider(classOf[Google2Api])
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .scope("https://www.googleapis.com/auth/youtube.readonly")
    .callback("https://www.example.com/oauth2callback")
    .build()
    
}