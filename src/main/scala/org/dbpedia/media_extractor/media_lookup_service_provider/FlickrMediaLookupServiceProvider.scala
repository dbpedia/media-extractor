package org.dbpedia.media_extractor.media_lookup_service_provider

import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import scala.collection.mutable.ListBuffer
import org.dbpedia.media_extractor.media_provider_session.FlickrSearchResult
import scala.xml.XML
import org.scribe.model.Response

class FlickrMediaLookupServiceProvider extends MediaLookupServiceProvider {

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, endPointRootUri)
    request.addQuerystringParameter("method", method)

    if (signRequest)
      oAuthService.signRequest(accessToken, request)

    request.send()
  }

  override def getSearchResults(searchResponse: Response): List[FlickrSearchResult] = {
    val myXml = XML.loadString(searchResponse.getBody())
    val resultsListBuffer = new ListBuffer[FlickrSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += FlickrSearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

}