package org.dbpedia.media_extractor.oauthsession

import org.dbpedia.media_extractor.search_result.FlickrSearchResult
import org.scribe.builder.api.FlickrApi

class FlickrOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession[FlickrApi](
    myProviderApi = new FlickrApi,
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile)

object FlickrOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
