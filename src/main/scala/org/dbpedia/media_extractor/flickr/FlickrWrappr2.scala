package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import com.hp.hpl.jena.rdf.model.ModelFactory
import scala.xml.Elem

class FlickrWrappr2(val serverRootUri: String = "http://localhost/flickrwrappr/", val flickrCredentialsFile: String = "/flickr.setup.properties") {
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"
  val flickrOAuthSession = FlickrOAuthSession(flickrCredentialsFile)
}

object FlickrWrappr2 extends App {

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

  val flickrGeoSearch: FlickrSearch = new FlickrGeoSearch
  val flickrDBpediaSearch: FlickrSearch = new FlickrDBpediaSearch

  def apply(serverRootUri: String = "http://localhost/flickrwrappr/", flickrCredentialsFile: String = "/flickr.setup.properties") =
    new FlickrWrappr2(serverRootUri, flickrCredentialsFile)

  def generateUrisForFlickrSearchResponse(myXml: Elem): List[FlickrSearchResult] = {
    val resultsListBuffer = new ListBuffer[FlickrSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += FlickrSearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  // FIXME: buggy
  def performFlickrGeoSearch(latitude: String = lat, longitude: String = lon, searchRadius: String = radius) {
    addNameSpacesToGeoSearchRDFGraph()
    addFlickrGeoSearchResultsToGeoSearchRDFGraph(generateUrisForFlickrSearchResponse(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude, longitude, radius, license, signRequest)))
    addGeoSearchDocumentMetadataToGeoRDFGraph
    addGeoSearchLocationMetadataToGeoRDFGraph
  }

  // TODO: implement stub
  def performFlickrDBpediaSearch(targetResource: String, searchRadius: String = radius) {
  }

}

