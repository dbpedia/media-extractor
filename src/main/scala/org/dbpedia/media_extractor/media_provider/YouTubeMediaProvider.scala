package org.dbpedia.media_extractor.media_provider

import scala.collection.mutable.ListBuffer
import scala.xml.XML

import org.dbpedia.media_extractor.oauthsession.YouTubeOAuthSession
import org.dbpedia.media_extractor.search_result.YouTubeSearchResult
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class YouTubeMediaProvider(
  oAuthSession: YouTubeOAuthSession)

  extends MediaProvider[YouTubeSearchResult](
    oAuthSession) {

  override val termsOfUseUri = "https://www.youtube.com/t/terms"
  override val endPointRootUri = "https://www.googleapis.com/youtube/v3/"

  override val maxResultsPerQuery = "50" // according to YouTube API's TOU
  override val targetLicenses = "creativeCommon" // CC-BY + public domain - See details below

  /* CC BY License includes (from https://support.google.com/youtube/answer/2797468/?hl=en)
   * 
   * -Your originally created content
   * -Other videos marked with a CC BY license
   * -Videos in the public domain
   * 
   */

  override protected def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.GET, endPointRootUri + "search")

    // Documentation:
    // https://developers.google.com/youtube/v3/docs/search/list

    searchRequest.addQuerystringParameter("q", searchText)
    searchRequest.addQuerystringParameter("location", "(" + latitude + "," + longitude + ")")
    searchRequest.addQuerystringParameter("locationRadius", radius + measurementUnit)
    searchRequest.addQuerystringParameter("videoLicense", targetLicenses)
    searchRequest.addQuerystringParameter("maxResults", maxResultsPerQuery)
    searchRequest.addQuerystringParameter("order", "relevance")
    searchRequest.addQuerystringParameter("type", "video") // only look for videos; ignore playlists and channels
    searchRequest.addQuerystringParameter("publishedAfter", "1970-01-01T00:00:00Z")

    val myService = oAuthSession.myOAuthServiceBuilder.oAuthService

    // This request does not need to be signed
    if (signRequest)
      myService.signRequest(oAuthSession.accessToken, searchRequest)

    searchRequest.send()
  }

  override protected def getSearchResults(searchResponse: Response): Set[YouTubeSearchResult] = {
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
