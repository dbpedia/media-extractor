package org.dbpedia.media_extractor.media_provider.flickr

import org.dbpedia.media_extractor.lookup_provider.MediaProvider
import org.dbpedia.media_extractor.lookup_provider.MediaProviderOAuthSession
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

abstract class FlickrMediaProvider(
  override val targetLicenses: String,
  override val mediaProviderOAuthSession: MediaProviderOAuthSession[FlickrApi])

  extends MediaProvider(mediaProviderOAuthSession, targetLicenses) {
  // TODO: complete this empty stub

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, MediaProviderOAuthSession.endPointUri.toString())
    request.addQuerystringParameter("method", method)

    if (signRequest)
      mediaProviderOAuthSession.flickrOAuthService.signRequest(mediaProviderOAuthSession.accessToken, request)

    request.send()
  }

  def getFlickrSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, MediaProviderOAuthSession.endPointUri.toString())

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
      mediaProviderOAuthSession.flickrOAuthService.signRequest(mediaProviderOAuthSession.accessToken, searchRequest)

    searchRequest.send()
  }
}
