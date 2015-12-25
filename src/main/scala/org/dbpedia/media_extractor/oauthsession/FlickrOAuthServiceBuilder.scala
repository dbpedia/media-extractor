package org.dbpedia.media_extractor.oauthsession

import com.github.scribejava.apis.FlickrApi
import com.github.scribejava.core.oauth.OAuthService
import com.github.scribejava.core.builder.ServiceBuilder

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
