package org.dbpedia.media_extractor.oauthsession

class FlickrOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession(
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile) {

  override val myOAuthServiceBuilder = new FlickrOAuthServiceBuilder(myApiKey, myApiKeySecret)
}

object FlickrOAuthSession {
  def apply(
    savedCredentialsFile: String = "/flickr.setup.properties",
    savedAccessTokenFile: String = "/flickr.accessToken.properties") =
    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
