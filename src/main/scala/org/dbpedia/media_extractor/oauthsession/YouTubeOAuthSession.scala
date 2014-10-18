package org.dbpedia.media_extractor.oauthsession

class YouTubeOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession(
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile) {

  override val myOAuthServiceBuilder = new YouTubeOAuthServiceBuilder(myApiKey, myApiKeySecret)
}

object YouTubeOAuthSession {
  def apply(
    savedCredentialsFile: String = "/youtube.setup.properties",
    savedAccessTokenFile: String = "/youtube.accessToken.properties") =
    new YouTubeOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
