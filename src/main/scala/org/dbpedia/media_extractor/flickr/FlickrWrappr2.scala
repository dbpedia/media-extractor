package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem

import com.hp.hpl.jena.rdf.model.Model

case class SearchResult(depictionUri: String, pageUri: String)

object FlickrWrappr2 extends App {
  val geoRDFGraph = ModelFactory.createDefaultModel()
  val dbpediaRDFGraph = ModelFactory.createDefaultModel()

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

}
