package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.XML

import org.scribe.model.Response

import com.hp.hpl.jena.rdf.model.Model

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

case class FlickrSearchResult(depictionUri: String, pageUri: String)

abstract class FlickrLookup(val flickrOAuthSession: FlickrOAuthSession) {

  val flickrTermsUri = "https://secure.flickr.com/help/terms/"
  val lookupFooter = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

  protected def namespacesMap = Map(
    "foaf" -> "http://xmlns.com/foaf/0.1/",
    "dcterms" -> "http://purl.org/dc/terms/",
    "rdfs" -> "http://www.w3.org/2000/01/rdf-schema#",
    "wgs84_pos" -> "http://www.w3.org/2003/01/geo/wgs84_pos#")

  val flickrCredentialsFile: String = "/flickr.setup.properties"

  val license = "1,2"
  val radius = "5"
  val signRequest = true

  def validateFlickrSearchResponse(flickrSearchResponse: Response): Boolean = {
    flickrSearchResponse.getMessage().equals("OK")
  }

  def getFlickrSearchResults(flickrSearchResponse: Response): List[FlickrSearchResult] = {
    val myXml = XML.loadString(flickrSearchResponse.getBody())
    val resultsListBuffer = new ListBuffer[FlickrSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += FlickrSearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def addNameSpacesToRDFGraph(rdfGraph: Model) = namespacesMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

}
