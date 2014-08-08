package org.dbpedia.media_extractor.lookup_provider.flickr

import org.dbpedia.media_extractor.lookup_provider.LookupOAuthSession
import org.dbpedia.media_extractor.lookup_provider.LookupProvider
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Response

abstract class FlickrLookupProvider(
  val targetLicenses: String,
  val lookupOAuthSession: LookupOAuthSession[FlickrApi])

  extends LookupProvider(lookupOAuthSession, targetLicenses) {
  // TODO: complete this empty stub

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, LookupOAuthSession.endPointUri.toString())
    request.addQuerystringParameter("method", method)

    if (signRequest)
      lookupOAuthSession.flickrOAuthService.signRequest(lookupOAuthSession.accessToken, request)

    request.send()
  }

  def getFlickrSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, LookupOAuthSession.endPointUri.toString())

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("text", searchText)
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("radius", radius)
    searchRequest.addQuerystringParameter("license", license)
    searchRequest.addQuerystringParameter("per_page", "30") // maximum according to FlickrAPI's TOU
    searchRequest.addQuerystringParameter("sort", "relevance")
    searchRequest.addQuerystringParameter("min_taken_date", "1800-01-01 00:00:00") // limiting agent to avoid "parameterless searches"

    // This request does not need to be signed
    if (signRequest)
      lookupOAuthSession.flickrOAuthService.signRequest(lookupOAuthSession.accessToken, searchRequest)

    searchRequest.send()
  }
}
