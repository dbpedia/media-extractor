package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.Google20Api

class YouTubeOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession[Google20Api](
    myProviderApi = new Google20Api,
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile)

object YouTubeOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
