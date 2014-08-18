package org.dbpedia.media_extractor.lookup_service

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.scribe.builder.api.Api
import org.scribe.model.Response
import com.hp.hpl.jena.rdf.model.Model
import javax.naming.directory.SearchResult

abstract class LookupService[ProviderApi <: Api, SearchResultType <: SearchResult](
  mediaLookupServiceProviderCallback: MediaLookupServiceProvider[ProviderApi, SearchResultType],
  val radius: String = "5") {

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

  def addSearchResultsToRDFGraph[ResultType](searchResultsList: List[ResultType], rdfGraph: Model)

  def validateSearchResponse(searchResponse: Response): Boolean = {
    searchResponse.getMessage().equals("OK")
  }

  def addNameSpacesToRDFGraph(rdfGraph: Model) =
    namespaceUriMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

}
