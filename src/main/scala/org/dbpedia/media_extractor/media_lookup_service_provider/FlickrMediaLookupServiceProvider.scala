package org.dbpedia.media_extractor.media_lookup_service_provider

import scala.collection.mutable.ListBuffer
import scala.xml.XML
import org.dbpedia.media_extractor.search_result.FlickrSearchResult
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.query.QueryFactory
import scala.collection.mutable.ArrayBuffer
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF

class FlickrMediaLookupServiceProvider(

  savedCredentialsFile: String = "/flickr.setup.properties",
  savedAccessTokenFile: String = "/flickr.accessToken.properties")

  extends MediaLookupServiceProvider[FlickrApi, FlickrSearchResult](
    new FlickrApi,
    savedCredentialsFile,
    savedAccessTokenFile) {

  override val termsOfUseUri = "https://secure.flickr.com/help/terms/"
  override val endPointRootUri = "https://api.flickr.com/services/rest/"

  override val maxResultsPerQuery = "30" // according to FlickrAPI's TOU
  override val targetLicenses = "4,5,7,8" // See detail on licenses below

  /* Licenses (from https://secure.flickr.com/services/api/flickr.photos.licenses.getInfo.html)
   * 
   *<license id="0" name="All Rights Reserved" url="" />
   *<license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
   *<license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
   *<license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
   *<license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
   *<license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
   *<license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
   *<license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
   *<license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
   * 
   */

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, endPointRootUri)
    request.addQuerystringParameter("method", method)

    if (signRequest)
      oAuthSession.oAuthService.signRequest(oAuthSession.accessToken, request)

    request.send()
  }

  override def getSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointRootUri)

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("text", searchText)
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("radius", radius)
    searchRequest.addQuerystringParameter("radius_units", measurementUnit)
    searchRequest.addQuerystringParameter("license", targetLicenses)
    searchRequest.addQuerystringParameter("per_page", maxResultsPerQuery)
    searchRequest.addQuerystringParameter("sort", "relevance")
    searchRequest.addQuerystringParameter("min_taken_date", "1800-01-01 00:00:00") // limiting agent to avoid "parameterless searches"

    // This request does not need to be signed
    if (signRequest)
      oAuthSession.oAuthService.signRequest(oAuthSession.accessToken, searchRequest)

    searchRequest.send()
  }

  override def getSearchResults(searchResponse: Response): List[FlickrSearchResult] = {
    val myXml = XML.loadString(searchResponse.getBody())
    val resultsListBuffer = new ListBuffer[FlickrSearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += FlickrSearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def addSearchResultsToRDFGraph(searchResultsList: List[FlickrSearchResult], rdfGraph: Model) {
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri)
    for (resultElem <- searchResultsList) {
      val pageUriResource = rdfGraph.createResource(resultElem.getLinks())

      dbpediaMediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  override def performSemanticLookup(targetResource: String = targetResource, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    val wgs84_posNsUri = namespaceUriMap("wgs84_pos")

    // SPARQL source code for the query
    val sparqlQueryString = "PREFIX rdfs: <" + RDFS.getURI() + "> " +
      "PREFIX wgs84_pos: <" + wgs84_posNsUri + "> " +
      "SELECT ?label ?lat ?long " +
      "WHERE { " +
      "	<" + dbpediaResourceFullUri + "> rdfs:label ?label . " +
      "	OPTIONAL { " +
      "		<" + dbpediaResourceFullUri + "> wgs84_pos:lat ?lat . " +
      "		<" + dbpediaResourceFullUri + "> wgs84_pos:long ?long " +
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
      val processedLabels = ArrayBuffer[String]()

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

          // 1) Perform a Flickr search
          val flickrSearchResults = getFlickrSearchResults(
            flickrOAuthSession.getFlickrSearchResponse(
              searchText = label,
              latitude = lat,
              longitude = long,
              radius,
              license,
              signRequest))

          // 2) Add the Flickr results to the RDF graph
          addFlickrSearchResultsToRDFGraph(flickrSearchResults, rdfGraph)

          processedLabels += label
        }

      }

    } finally
      sparqlQueryExecution.close()

    rdfGraph
  }

}