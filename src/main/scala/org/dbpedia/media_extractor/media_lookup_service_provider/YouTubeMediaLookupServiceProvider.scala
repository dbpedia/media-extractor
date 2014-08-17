package org.dbpedia.media_extractor.media_lookup_service_provider

import org.scribe.model.Verb
import org.scribe.model.OAuthRequest
import org.scribe.model.Response

class YouTubeMediaLookupServiceProvider extends MediaLookupServiceProvider {

  //TODO: complete stub

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