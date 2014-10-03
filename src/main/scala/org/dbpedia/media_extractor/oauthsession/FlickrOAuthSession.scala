package org.dbpedia.media_extractor.oauthsession

class FlickrOAuthSession(
  savedCredentialsFile: String = "/flickr.setup.properties",
  savedAccessTokenFile: String = "/flickr.accessToken.properties")
  extends OAuthSession(
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile) {

  override val myOAuthServiceBuilder = new FlickrOAuthServiceBuilder(myApiKey, myApiKeySecret)
}

object FlickrOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
