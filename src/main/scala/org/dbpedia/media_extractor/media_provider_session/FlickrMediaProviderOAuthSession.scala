package org.dbpedia.media_extractor.media_provider_session

import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class FlickrMediaProviderOAuthSession(
  savedCredentialsFile: String = "/flickr.setup.properties",
  savedAccessTokenFile: String = "/flickr.accessToken.properties")

  extends MediaProviderOAuthSession[FlickrApi](
    myApi = new FlickrApi,
    targetLicenses = "4,5,7,8", // See detail on licenses below
    savedCredentialsFile,
    savedAccessTokenFile) {

  /* Licenses (from https://secure.flickr.com/services/api/flickr.photos.licenses.getInfo.html)
   * 
   *<license id="0" name="All Rights Reserved" url="" />
   *<license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
   *<license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
   *<license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
   *<license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
   *<license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
   *<license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
   *<license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
   *<license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
   * 
   */

  val endPointRootUri = "https://api.flickr.com/services/rest/"

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, endPointRootUri)
    request.addQuerystringParameter("method", method)

    if (signRequest)
      oAuthService.signRequest(accessToken, request)

    request.send()
  }

  def getFlickrSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointRootUri)

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
      oAuthService.signRequest(accessToken, searchRequest)

    searchRequest.send()
  }

}

object FlickrMediaProviderOAuthSession {

  def apply(
    savedCredentialsFile: String = "/flickr.setup.properties",
    savedAccessTokenFile: String = "/flickr.accessToken.properties") =

    new FlickrMediaProviderOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)

}

 
