package org.dbpedia.media_extractor.oauthsession

class YouTubeOAuthSession(
  savedCredentialsFile: String = "/youtube.setup.properties",
  savedAccessTokenFile: String = "/youtube.accessToken.properties")
  extends OAuthSession(
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile) {

  override val myOAuthServiceBuilder = new YouTubeOAuthServiceBuilder(myApiKey, myApiKeySecret)

}

object YouTubeOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new YouTubeOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
