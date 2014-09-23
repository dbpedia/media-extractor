package org.dbpedia.media_extractor.media_provider

import scala.collection.mutable.ListBuffer
import scala.xml.XML

import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.search_result.VimeoSearchResult
import org.scribe.builder.api.VimeoApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class VimeoMediaProvider(

  savedCredentialsFile: String = "/vimeo.setup.properties",
  savedAccessTokenFile: String = "/vimeo.accessToken.properties",
  oAuthSession: OAuthSession[VimeoApi])

  extends MediaProvider[VimeoApi, VimeoSearchResult](
    new VimeoApi,
    oAuthSession,
    savedCredentialsFile,
    savedAccessTokenFile) {

  override val termsOfUseUri = "https://vimeo.com/terms/"
  override val endPointRootUri = "https://vimeo.com/api/rest/"

  override val maxResultsPerQuery = "50" // maximum per page according to Vimeo API's doc
  override val targetLicenses = "CC-BY-SA" // can only be one - See details on licenses below

  /* Licenses (from https://developer.vimeo.com/api/endpoints/videos#)
   * 
   * CC
   * CC-BY
   * CC-BY-SA
   * CC-BY-ND
   * CC-BY-NC
   * CC-BY-NC-SA
   * CC-BY-NC-ND
   * in-progress
   * 
   */


  override def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointRootUri)

    searchRequest.addQuerystringParameter("method", "vimeo.")
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

  override def getSearchResults(searchResponse: Response): Set[VimeoSearchResult] = {
    val myXml = XML.loadString(searchResponse.getBody())
    val resultsListBuffer = new ListBuffer[VimeoSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val link = 
        val pageSearchResult = new VimeoSearchResult(link)

        resultsListBuffer += pageSearchResult
    }
    resultsListBuffer.toSet
  }

}
