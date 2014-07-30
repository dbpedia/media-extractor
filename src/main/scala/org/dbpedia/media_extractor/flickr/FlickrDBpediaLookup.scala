package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ArrayBuffer

import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
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

  def performFlickrLookup(targetResource: String = targetResource, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    // The SPARQL query
    val sparqlQueryString =
      "PREFIX rdfs: <" + RDFS.getURI() + "> " +
        "PREFIX wgs84_pos: <" + namespacesMap("wgs84_pos") + "> " +
        "SELECT ?label ?lat ?long " +
        "FROM <" + dbpediaRootUri + "> " +
        "WHERE { " +
        "	<" + dbpediaResourceFullUri + "> rdfs:label ?label . " +
        "	OPTIONAL { " +
        "		<" + dbpediaResourceFullUri + "> wgs84_pos:lat ?lat . " +
        "		<" + dbpediaResourceFullUri + "> wgs84_pos:long ?long " +
        "	} " +
        "}"

    // Prepare the SPARQL query
    val sparqlQuery = QueryFactory.create(sparqlQueryString)
    val sparqlQueryExecution = QueryExecutionFactory.create(sparqlQuery, rdfGraph)

    try {
      // Execute the SPARQL query
      val sparqlQueryResultSet = sparqlQueryExecution.execSelect()

      val noResults = !sparqlQueryResultSet.hasNext()

      // Some labels are common to many languages (e.g. "Buenos Aires")
      val processedLabels = ArrayBuffer[String]()

      ResultSetFormatter.asRDF(rdfGraph, sparqlQueryResultSet);

      // For each solution to the query
      while (sparqlQueryResultSet.hasNext()) {
        val querySolution = sparqlQueryResultSet.nextSolution()

        // Skip duplicated labels
        if (!(processedLabels.contains(querySolution.getLiteral("label").toString()))) {

          // 1) Perform a Flickr search
          val flickrSearchResults = getFlickrSearchResults(
            flickrOAuthSession.getFlickrSearchResponse(
              searchText = querySolution.getLiteral("label").toString(),
              latitude = querySolution.getLiteral("lat").toString(),
              longitude = querySolution.getLiteral("long").toString(),
              radius,
              license,
              signRequest))

          // 2) Add the Flickr results to the RDF graph
          addFlickrSearchResultsToRDFGraph(flickrSearchResults, rdfGraph)

          processedLabels += querySolution.getLiteral("label").toString()
        }
      }

    } finally
      sparqlQueryExecution.close()

    rdfGraph
  }

}
