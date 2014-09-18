package org.dbpedia.media_extractor.media_provider

import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.search_result.SearchResult
import org.scribe.builder.api.Api
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryExecutionFactory
import scala.collection.mutable.ArrayBuffer
import org.scribe.model.Response
import com.hp.hpl.jena.rdf.model.Model

class MediaProvider[ProviderApi <: Api, SearchResultType <: SearchResult](
  val myProviderApi: ProviderApi,
  val savedCredentialsFile: String,
  val savedAccessTokenFile: String) {

  val oAuthSession = OAuthSession(
    myProviderApi,
    savedCredentialsFile,
    savedAccessTokenFile)

  val measurementUnit = "km"

  val termsOfUseUri: String
  val endPointRootUri: String
  val maxResultsPerQuery: String
  val targetLicenses: String

  val dbpediaRootUri = "http://dbpedia.org/"
  val dbpediaMediaRootUri = "http://media.dbpedia.org/"
  val dbpediaResourceRootUri = dbpediaRootUri + "resource/"
  val dbpediaMediaResourceRootUri = dbpediaMediaRootUri + "resource/"

  def encodeResourceLeafUri(targetResource: String) = targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")
  def dbpediaResourceFullUri(targetResource: String) = dbpediaResourceRootUri + encodeResourceLeafUri(targetResource)
  def dbpediaMediaResourceFullUri(targetResource: String) = dbpediaMediaResourceRootUri + encodeResourceLeafUri(targetResource)

  def getSearchResults(searchResponse: Response): Set[SearchResultType]

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
          val searchResponse = oAuthSession.getSearchResponse(
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

}

object MediaProvider {

  def apply[ProviderApi, SearchResultType](
    myProviderApi: ProviderApi,
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =

    new MediaProvider[ProviderApi, SearchResultType](
      myProviderApi,
      savedCredentialsFile,
      savedAccessTokenFile)
}
