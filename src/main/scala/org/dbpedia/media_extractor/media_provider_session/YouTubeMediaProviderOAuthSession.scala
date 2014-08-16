package org.dbpedia.media_extractor.media_provider_session

import org.scribe.builder.api.GoogleApi

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

  val endPointRootUri = "https://www.googleapis.com/youtube/v3"

  // TODO: complete this empty stub
}
