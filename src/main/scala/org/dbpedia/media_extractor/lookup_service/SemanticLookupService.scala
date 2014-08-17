package org.dbpedia.media_extractor.lookup_service

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.dbpedia.media_extractor.search_result.SearchResult
import org.scribe.builder.api.Api
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.OWL
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

abstract class SemanticLookupService[ProviderApi <: Api, SearchResultType <: SearchResult](
  // By default, search for Brussels
  val targetResource: String = "Brussels",
  radius: String = "5",
  mediaLookupServiceProviderCallback: MediaLookupServiceProvider[ProviderApi])

  extends LookupService[ProviderApi, SearchResultType](
    mediaLookupServiceProviderCallback,
    radius) {

  val dbpediaResourceRootUri = dbpediaRootUri + "resource/"
  val dbpediaMediaResourceRootUri = dbpediaMediaRootUri + "resource/"

  val resourceLeafUri = targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  val dbpediaResourceFullUri = dbpediaResourceRootUri + resourceLeafUri
  val dbpediaMediaResourceFullUri = dbpediaMediaResourceRootUri + resourceLeafUri

  def performSemanticLookup(targetResource: String = "Brussels", radius: String = radius): Model

  def addSearchResultsToRDFGraph(searchResultsList: List[SearchResult], rdfGraph: Model)

  def addMetadataToRDFGraph(rdfGraph: Model) = {
    addDocumentMetadataToRDFGraph(rdfGraph)
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model) = {

    val lookupHeader = "Photos for Dbpedia resource " + targetResource

    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")

    val foafUri = namespaceUriMap("foaf")

    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri)
    val flickrTermsUriResource = rdfGraph.createResource(mediaLookupServiceProviderCallback.termsOfUseUri)
    val dbpediaMediaRootUriResource = rdfGraph.createResource(dbpediaMediaRootUri)

    dbpediaMediaRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
    dbpediaMediaResourceFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)
    dbpediaMediaResourceFullUriResource.addProperty(RDF.`type`, foafUri + "Document")
    dbpediaMediaResourceFullUriResource.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)
    dbpediaMediaResourceFullUriResource.addProperty(DCTerms.license, flickrTermsUriResource)
    dbpediaMediaResourceFullUriResource.addProperty(FOAF.maker, dbpediaMediaRootUriResource)

    dbpediaMediaResourceFullUriResource.addProperty(OWL.sameAs, dbpediaResourceFullUriResource)
  }

}
