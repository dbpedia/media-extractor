package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.Google2Api

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
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new YouTubeOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
