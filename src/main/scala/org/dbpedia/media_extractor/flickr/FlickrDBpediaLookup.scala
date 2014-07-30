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

  val dbpediaResourceUri = "http://dbpedia.org/resource/"
  val dbpediaResourceFullUri = dbpediaResourceUri + targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  def addFlickrSearchResultsToRDFGraph(rdfGraph: Model, flickrSearchResultsList: List[FlickrSearchResult], dbpediaResourceFullUriResource: Resource) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addMetadataToRDFGraph(rdfGraph: Model, dbpediaResourceFullUriResource: Resource) = {
    addDocumentMetadataToRDFGraph(rdfGraph, dbpediaResourceFullUriResource)
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model, dbpediaResourceFullUriResource: Resource) = {
    val foafDocumentResource2 = rdfGraph.createResource(dbpediaResourceFullUri)
    foafDocumentResource2.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val label2 = "Photos for Dbpedia resource " + targetResource
    val labelLiteral2 = rdfGraph.createLiteral(label2, "en")
    foafDocumentResource2.addProperty(RDFS.label, labelLiteral2)

    foafDocumentResource2.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)

    val flickrTOUResource2 = rdfGraph.createResource(flickrTermsUri)
    foafDocumentResource2.addProperty(DCTerms.license, flickrTOUResource2)

    dbpediaResourceFullUriResource.addProperty(RDFS.label, labelLiteral2)

    val flickrwrapprLiteral2 = rdfGraph.createLiteral(lookupFooter, "en")
    val serverRootUriResource2 = rdfGraph.createResource(serverRootUri)
    serverRootUriResource2.addProperty(RDFS.label, flickrwrapprLiteral2)
  }

  // FIXME: logic is incorrect
  def performFlickrLookup(targetResource: String, radius: String): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()
    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)

    // TODO: perform SPARQL query

    // TODO: process query results

    val flickrSearchResults = getFlickrSearchResults(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude = "", longitude = "", radius, license, signRequest))

    addNameSpacesToRDFGraph(rdfGraph: Model)
    addMetadataToRDFGraph(rdfGraph: Model, dbpediaResourceFullUriResource)

    addFlickrSearchResultsToRDFGraph(rdfGraph, flickrSearchResults, dbpediaResourceFullUriResource)

    rdfGraph
  }

}
