package org.dbpedia.media_extractor.flickr

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

case class FlickrDBpediaLookup(
  // By default, search for Brussels
  val targetResource: String = "Brussels",
  val serverRootUri: String,
  override val flickrOAuthSession: FlickrOAuthSession)

  extends FlickrLookup(flickrOAuthSession) {

  val dbpediaRootUri = "http://dbpedia.org/"
  val dbpediaResourceUri = dbpediaRootUri + "resource/"
  val dbpediaResourceFullUri = dbpediaResourceUri + targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  def addFlickrSearchResultsToRDFGraph(flickrSearchResultsList: List[FlickrSearchResult], rdfGraph: Model) {
    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addMetadataToRDFGraph(rdfGraph: Model) = {
    addDocumentMetadataToRDFGraph(rdfGraph)
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model) = {
    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)
    val foafDocumentResource = rdfGraph.createResource(dbpediaResourceFullUri)
    foafDocumentResource.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val lookupHeader = "Photos for Dbpedia resource " + targetResource
    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    foafDocumentResource.addProperty(RDFS.label, lookupHeaderLiteral)

    foafDocumentResource.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)

    val flickrTOUResource = rdfGraph.createResource(flickrTermsUri)
    foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

    dbpediaResourceFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)

    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    serverRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
  }

  // FIXME: logic is incorrect
  def performFlickrLookup(targetResource: String, radius: String): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()
    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)

    // TODO: perform SPARQL query

    // TODO: process query results

    val flickrSearchResults = getFlickrSearchResults(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude = "", longitude = "", radius, license, signRequest))

    addNameSpacesToRDFGraph(rdfGraph: Model)
    addMetadataToRDFGraph(rdfGraph: Model, dbpediaResourceFullUri)

    addFlickrSearchResultsToRDFGraph(rdfGraph, flickrSearchResults, dbpediaResourceFullUri)

    rdfGraph
  }

}
