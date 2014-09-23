package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.Google2Api

class YouTubeOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession[Google2Api](
    myProviderApi = new Google2Api,
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile,
    useRequestToken = false)

object YouTubeOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new YouTubeOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
