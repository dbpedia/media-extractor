package org.dbpedia.media_extractor.flickrwrappr2

import scala.collection.mutable.ArrayBuffer

import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.OWL
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

case class FlickrDBpediaLookup(
  // By default, search for Brussels
  val targetResource: String = "Brussels",
  val serverRootUri: String,
  override val flickrOAuthSession: FlickrOAuthSession)

  extends FlickrLookup(flickrOAuthSession) {

  val dbpediaRootUri = "http://dbpedia.org/"
  val dbpediaResourceRootUri = dbpediaRootUri + "resource/"

  val dbpediaMediaRootUri = "http://media.dbpedia.org/"
  val dbpediaMediaResourceRootUri = dbpediaMediaRootUri + "resource/"

  val resourceLeafUri = targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  val dbpediaResourceFullUri = dbpediaResourceRootUri + resourceLeafUri
  val dbpediaMediaResourceFullUri = dbpediaMediaResourceRootUri + resourceLeafUri

  val photosRootUri = serverRootUri + "photos/"

  val photosFullUri = photosRootUri + resourceLeafUri

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

    val lookupHeader = "Photos for Dbpedia resource " + targetResource

    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")

    val foafUri = namespaceUriMap("foaf")

    val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri)
    val photosFullUriResource = rdfGraph.createResource(photosFullUri)
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    val flickrTermsUriResource = rdfGraph.createResource(flickrTermsUri)

    serverRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
    photosFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)
    photosFullUriResource.addProperty(RDF.`type`, foafUri + "Document")
    photosFullUriResource.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)
    photosFullUriResource.addProperty(DCTerms.license, flickrTermsUriResource)
    photosFullUriResource.addProperty(FOAF.maker, serverRootUriResource)

    dbpediaResourceFullUriResource.addProperty(OWL.sameAs, dbpediaMediaResourceFullUriResource)
  }

  def performFlickrLookup(targetResource: String = targetResource, radius: String = radius): Model = {
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
