package org.dbpedia.media_extractor.media_provider

import scala.collection.mutable.ListBuffer
import scala.xml.XML

import org.dbpedia.media_extractor.search_result.FlickrSearchResult
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class YouTubeMediaProvider(

  savedCredentialsFile: String = "/youtube.setup.properties",
  savedAccessTokenFile: String = "/youtube.accessToken.properties")

  extends MediaProvider[Google20Api, YouTubeSearchResult](
    new Google20Api,
    savedCredentialsFile,
    savedAccessTokenFile) {

  override val termsOfUseUri = ""
  override val endPointRootUri = ""

  override val maxResultsPerQuery = "" // according to YouTube API's TOU
  override val targetLicenses = "" // See detail on licenses below

  /* Licenses (from )
   * 
   *   * 
   */

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, endPointRootUri)
    request.addQuerystringParameter("method", method)

    if (signRequest)
      oAuthSession.oAuthService.signRequest(oAuthSession.accessToken, request)

    request.send()
  }

  override def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointRootUri)

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("text", searchText)
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("radius", radius)
    searchRequest.addQuerystringParameter("radius_units", measurementUnit)
    searchRequest.addQuerystringParameter("license", targetLicenses)
    searchRequest.addQuerystringParameter("per_page", maxResultsPerQuery)
    searchRequest.addQuerystringParameter("sort", "relevance")
    searchRequest.addQuerystringParameter("min_taken_date", "1800-01-01 00:00:00") // limiting agent to avoid "parameterless searches"

    // This request does not need to be signed
    if (signRequest)
      oAuthSession.oAuthService.signRequest(oAuthSession.accessToken, searchRequest)

    searchRequest.send()
  }

  override def getSearchResults(searchResponse: Response): Set[YouTubeSearchResult] = {
    val myXml = XML.loadString(searchResponse.getBody())
    val resultsListBuffer = new ListBuffer[YouTubeSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val thumbnailUri = ""
        val pageSearchResult = new YouTubeSearchResult(thumbnailUri)

        resultsListBuffer += pageSearchResult
    }
    resultsListBuffer.toSet
  }

}
