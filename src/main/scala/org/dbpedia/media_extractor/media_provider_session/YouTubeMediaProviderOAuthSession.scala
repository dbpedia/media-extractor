package org.dbpedia.media_extractor.media_provider_session

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

  override def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.GET, endPointRootUri)

    searchRequest.addQuerystringParameter("method", "search")
    searchRequest.addQuerystringParameter("part", "snippet")
    searchRequest.addQuerystringParameter("type", "video")
    searchRequest.addQuerystringParameter("q", searchText)
    searchRequest.addQuerystringParameter("location", latitude + "," + longitude)
    searchRequest.addQuerystringParameter("locationRadius", radius + measurementUnit)
    searchRequest.addQuerystringParameter("videoLicense", license)
    searchRequest.addQuerystringParameter("safeSearch", "none") // no YouTube censorship
    searchRequest.addQuerystringParameter("maxResults", maxResultsPerQuery)
    searchRequest.addQuerystringParameter("order", "relevance")
    searchRequest.addQuerystringParameter("publishedAfter", "1970-01-01T00:00:00Z")

    // This request does not need to be signed
    if (signRequest)
      oAuthService.signRequest(accessToken, searchRequest)

    searchRequest.send()
  }

}

object YouTubeMediaProviderOAuthSession {

  def apply(
    savedCredentialsFile: String = "/youtube.setup.properties",
    savedAccessTokenFile: String = "/youtube.accessToken.properties") =

    new YouTubeMediaProviderOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)

}

