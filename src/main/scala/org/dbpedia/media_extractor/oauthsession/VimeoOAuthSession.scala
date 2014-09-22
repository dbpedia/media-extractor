package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.VimeoApi

class VimeoOAuthSession(
  savedCredentialsFile: String,
  savedAccessTokenFile: String)
  extends OAuthSession[VimeoApi](
    myProviderApi = new VimeoApi,
    savedCredentialsFile = savedCredentialsFile,
    savedAccessTokenFile = savedAccessTokenFile)

object VimeoOAuthSession {
  def apply(
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =
    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
