package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.GoogleApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class YouTubeMediaProviderOAuthSession(
  savedCredentialsFile: String = "/youtube.setup.properties",
  savedAccessTokenFile: String = "/youtube.accessToken.properties")

  extends MediaProviderOAuthSession[GoogleApi](
    myApi = new GoogleApi,
    targetLicenses = "creativeCommon", // CC-BY (see details below)
    savedCredentialsFile,
    savedAccessTokenFile) {

  /*
   * Accepted licenses in YouTube are:
   * (according to https://developers.google.com/youtube/v3/docs/videos#properties)
   * 
   * 1) YouTube license
   * 2) Creative Commons Attribution License
   * (see https://support.google.com/youtube/answer/2797468?hl=en)
   * 
   */

  override val endPointRootUri = "https://www.googleapis.com/youtube/v3"
  override val maxResultsPerQuery = "50"
  override val termsOfUseUri = "https://developers.google.com/youtube/terms"

}

object YouTubeMediaProviderOAuthSession {

  def apply(
    savedCredentialsFile: String = "/youtube.setup.properties",
    savedAccessTokenFile: String = "/youtube.accessToken.properties") =

    new YouTubeMediaProviderOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)

}

