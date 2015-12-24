package org.dbpedia.media_extractor.media_provider

import scala.collection.mutable.ListBuffer

import org.dbpedia.media_extractor.oauthsession.YouTubeOAuthSession
import org.dbpedia.media_extractor.search_result.YouTubeSearchResult
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb

import net.liftweb.json.parse

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

  override def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response = {

    val myOAuthServiceBuilder = oAuthSession.myOAuthServiceBuilder
    val searchRequest = new OAuthRequest(Verb.POST, endPointRootUri + "search", myOAuthServiceBuilder.oAuthService)

    // Documentation:
    // https://developers.google.com/youtube/v3/docs/search/list

    searchRequest.addQuerystringParameter("part", "snippet")
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

  override def getSearchResults(searchResponse: Response): Set[YouTubeSearchResult] = {

    val JSONString = searchResponse.getBody()
    val jsonJValue = parse(JSONString)

    val resultsListBuffer = new ListBuffer[YouTubeSearchResult]

    (jsonJValue \\ "items").children foreach {
      element =>
        val videoPageUri = "https://youtube.com/watch?v=" + (element \\ "id" \ "@videoId")
        val pageSearchResult = new YouTubeSearchResult(videoPageUri)
        resultsListBuffer += pageSearchResult
    }
    resultsListBuffer.toSet
  }

}
