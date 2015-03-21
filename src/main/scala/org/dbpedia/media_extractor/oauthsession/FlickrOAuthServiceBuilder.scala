package org.dbpedia.media_extractor.oauthsession

import ru.hh.oauth.subscribe.apis.FlickrApi
import ru.hh.oauth.subscribe.core.oauth.OAuthService
import ru.hh.oauth.subscribe.core.builder.ServiceBuilder

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
