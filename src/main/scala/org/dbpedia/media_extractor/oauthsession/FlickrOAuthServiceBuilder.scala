package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.oauth.OAuthService

class FlickrOAuthServiceBuilder(
  myApiKey: String,
  myApiKeySecret: String)
  extends {

    override val oAuthService: OAuthService = new ServiceBuilder()
      .provider(classOf[FlickrApi])
      .apiKey(myApiKey)
      .apiSecret(myApiKeySecret)
      .build()

  } with OAuthServiceBuilder(myApiKey, myApiKeySecret) {

  override val providerName = "Flickr"
}
