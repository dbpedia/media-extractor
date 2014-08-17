package org.dbpedia.media_extractor.media_provider

import org.dbpedia.media_extractor.media_provider_session.MediaProviderOAuthSession

abstract class LookupService(
  val mediaProviderOAuthSession: MediaProviderOAuthSession[T],
  val radius: String = "5") {
  // TODO: complete this empty stub

  val lookupFooter = "Media Extractor (inspired by FlickrWrappr)"

  val dbpediaRootUri = "http://dbpedia.org/"
  val dbpediaMediaRootUri = "http://media.dbpedia.org/"

  val outputPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "N-TRIPLES"

  val signRequest = true

  protected def namespaceUriMap = Map(
    "foaf" -> "http://xmlns.com/foaf/0.1/",
    "dcterms" -> "http://purl.org/dc/terms/",
    "rdfs" -> "http://www.w3.org/2000/01/rdf-schema#",
    "wgs84_pos" -> "http://www.w3.org/2003/01/geo/wgs84_pos#")

  def validateSearchResponse(flickrSearchResponse: Response): Boolean = {
    flickrSearchResponse.getMessage().equals("OK")
  }

  def addNameSpacesToRDFGraph(rdfGraph: Model) =
    namespaceUriMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

}
