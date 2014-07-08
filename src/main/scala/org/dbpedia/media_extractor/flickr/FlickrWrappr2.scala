package org.dbpedia.media_extractor.flickr

import java.net.URI

import scala.collection.mutable.ListBuffer
import scala.xml.Elem

import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

case class SearchResult(depictionUri: String, pageUri: String)

object FlickrWrappr2 extends App {
  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def generateLinksList(myXml: Elem): List[SearchResult] = {
    val resultsListBuffer = new ListBuffer[SearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += SearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def getFlickrSearchResponse(text: String = "", latitude: String = "", longitude: String = "", license: String = ""): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("license", license)
    searchRequest.addQuerystringParameter("per_page", "30") // maximum according to FlickrAPI's TOU
    searchRequest.addQuerystringParameter("sort", "relevance")

    // This request does not need to be signed
    searchRequest.send()
  }

  def validateFlickrSearchResponse(flickrSearchResponse: Response): Boolean = {
    flickrSearchResponse.getCode().equals("200")
  }
}
