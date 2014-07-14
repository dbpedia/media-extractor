package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory

case class SearchResult(depictionUri: String, pageUri: String)

class FlickrWrappr2(val serverRootUri: String)

object FlickrWrappr2 extends App {
  val geoRDFGraph = ModelFactory.createDefaultModel()
  val dbpediaRDFGraph = ModelFactory.createDefaultModel()

  val defaultServerRootUri = "http://localhost/flickrwrappr/"

  val locationRootUri = defaultServerRootUri + "location/"
  val dataRootUri = defaultServerRootUri + "data/photosDepictingLocation/"

  // By default, search for Brussels
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + lat + geoPath

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

  def apply(serverRootUri: String = defaultServerRootUri) = new FlickrWrappr2(serverRootUri)

  def generateUrisForFlickrSearchResponse(myXml: Elem): List[SearchResult] = {
    val resultsListBuffer = new ListBuffer[SearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += SearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def addNameSpacesToRDFGraph(nsMap: Map[String, String], rdfGraph: Model) =
    nsMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

  // TODO: implement stub 
  def performFlickrGeoSearch(latitude: String = lat, longitude: String = lon, searchRadius: String = radius) {
  }

  // TODO: implement stub
  def performFlickrDBpediaSearch(resource: String, searchRadius: String = radius) {
  }

  // TODO: implement stub
  def processFlickrGeoSearchResults

  // TODO: implement stub
  def processFlickrDBpediaSearchResults

  // TODO: implement stub
  def addGeoLocationMetadataToRDFGraph

  // TODO: implement stub
  def addGeoSearchDocumentMetadataToRDFGraph

  // TODO: implement stub
  def addDBpediaSearchDocumentMetadataToRDFGraph
}

