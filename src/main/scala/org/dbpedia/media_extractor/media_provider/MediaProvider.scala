package org.dbpedia.media_extractor.media_provider

import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.search_result.SearchResult

import com.github.scribejava.core.model.Response

import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.OWL
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

abstract class MediaProvider[SearchResultType <: SearchResult](
  val oAuthSession: OAuthSession) {

  val termsOfUseUri: String
  val endPointRootUri: String
  val maxResultsPerQuery: String
  val targetLicenses: String

  protected val measurementUnit = "km"

  private val dbpediaRootUri = "http://dbpedia.org/"
  private val dbpediaMediaRootUri = "http://media.dbpedia.org/"
  private val dbpediaResourceRootUri = dbpediaRootUri + "resource/"
  private val dbpediaMediaResourceRootUri = dbpediaMediaRootUri + "resource/"

  private def encodeResourceLeafUri(targetResource: String) = targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")
  private def dbpediaResourceFullUri(targetResource: String) = dbpediaResourceRootUri + encodeResourceLeafUri(targetResource)
  private def dbpediaMediaResourceFullUri(targetResource: String) = dbpediaMediaResourceRootUri + encodeResourceLeafUri(targetResource)

  def getSearchResults(searchResponse: Response): Set[SearchResultType]
  def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response

  def performLookup(targetResource: String, radius: String): Set[SearchResultType] = {

    val signRequest = true

    val WGS84_getURI = "http://www.w3.org/2003/01/geo/wgs84_pos#"

    // SPARQL source code for the query
    val sparqlQueryString =
      "PREFIX rdfs: <" + RDFS.getURI + "> " +
        "PREFIX wgs84_pos: <" + WGS84_getURI + "> " +
        "SELECT ?label ?lat ?long " +
        "WHERE { " +
        "	<" + dbpediaResourceFullUri(targetResource) + "> rdfs:label ?label . " +
        "	OPTIONAL { " +
        "		<" + dbpediaResourceFullUri(targetResource) + "> wgs84_pos:lat ?lat . " +
        "		<" + dbpediaResourceFullUri(targetResource) + "> wgs84_pos:long ?long " +
        "	} " +
        "}"

    // Create the SPARQL query
    val sparqlQuery = QueryFactory.create(sparqlQueryString)

    // Prepare to execute the query
    val sparqlQueryExecution = QueryExecutionFactory
      .sparqlService("http://dbpedia.org/sparql", sparqlQuery)

    try {
      // Execute the SPARQL query
      val sparqlQueryResultSet = sparqlQueryExecution.execSelect()

      // Some labels are common to many languages (e.g. "Buenos Aires")
      val processedLabels = Set[String]()

      val lookupResults = Set[SearchResultType]()

      // For each solution to the query
      while (sparqlQueryResultSet.hasNext()) {
        val querySolution = sparqlQueryResultSet.next()

        val label = querySolution.get("label").toString()
        val lat = querySolution.getLiteral("lat").getFloat().toString()
        val long = querySolution.getLiteral("long").getFloat().toString()

        // FIXME: save the whole triple (label, lat, long) as "processed".
        // Some labels have different coordinates across languages (e.g. "La Paz")

        // Skip duplicated labels
        if (!(processedLabels.contains(label))) {
          // Perform a Media search
          val searchResponse = getSearchResponse(
            searchText = label,
            latitude = lat,
            longitude = long,
            radius,
            signRequest)

          lookupResults ++: getSearchResults(searchResponse)
          processedLabels ++: label
        }

      }
      lookupResults
    } finally
      sparqlQueryExecution.close()
  }

  def constructRDFGraph(
    targetResource: String,
    lookupResults: Set[SearchResultType]): Model = {

    val rdfGraph = ModelFactory.createDefaultModel()
    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(targetResource, rdfGraph)
    addLookupResultsToRDFGraph(targetResource, lookupResults, rdfGraph)
    rdfGraph
  }

  private val namespaceUriMap = Map(
    "foaf" -> "http://xmlns.com/foaf/0.1/",
    "dcterms" -> "http://purl.org/dc/terms/",
    "rdfs" -> "http://www.w3.org/2000/01/rdf-schema#",
    "wgs84_pos" -> "http://www.w3.org/2003/01/geo/wgs84_pos#")

  private def addNameSpacesToRDFGraph(rdfGraph: Model) =
    namespaceUriMap.foreach {
      case (k, v) => rdfGraph.setNsPrefix(k, v)
    }

  private def addLookupResultsToRDFGraph(
    targetResource: String,
    searchResultsSet: Set[SearchResultType],
    rdfGraph: Model): Model = {
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri(targetResource))
    for (resultElem <- searchResultsSet) {
      val resultElemResource = rdfGraph.createResource(resultElem.getUri())
      dbpediaMediaResourceFullUriResource.addProperty(resultElem.getProperty(), resultElemResource)
    }
    rdfGraph
  }

  private def addMetadataToRDFGraph(targetResource: String, rdfGraph: Model) = {
    val lookupHeader = "Photos for Dbpedia resource " + targetResource
    val lookupFooter = "Media Extractor"
    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")
    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri(targetResource))
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri(targetResource))
    val termsOfUseUriResource = rdfGraph.createResource(termsOfUseUri)
    val dbpediaMediaRootUriResource = rdfGraph.createResource(dbpediaMediaRootUri)
    dbpediaMediaRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
    dbpediaMediaResourceFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)
    dbpediaMediaResourceFullUriResource.addProperty(RDF.`type`, FOAF.getURI() + "Document")
    dbpediaMediaResourceFullUriResource.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)
    dbpediaMediaResourceFullUriResource.addProperty(DCTerms.license, termsOfUseUriResource)
    dbpediaMediaResourceFullUriResource.addProperty(FOAF.maker, dbpediaMediaRootUriResource)
    dbpediaMediaResourceFullUriResource.addProperty(OWL.sameAs, dbpediaResourceFullUriResource)
  }

}
