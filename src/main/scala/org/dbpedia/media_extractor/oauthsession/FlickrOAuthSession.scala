package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.FlickrApi
import org.dbpedia.media_extractor.search_result.FlickrSearchResult

class FlickrOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession[FlickrApi, FlickrSearchResult](
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
