package org.dbpedia.media_extractor.media_lookup_service_provider

import org.dbpedia.media_extractor.media_provider_session.FlickrMediaProviderOAuthSession

class FlickrSemanticMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: FlickrMediaProviderOAuthSession,
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends FlickrMediaLookupServiceProvider {

  //TODO: complete stub

  def addSearchResultsToRDFGraph(searchResultsList: List[SearchResult], rdfGraph: Model) {
    val dbpediaMediaResourceFullUriResource = rdfGraph.createResource(dbpediaMediaResourceFullUri)
    for (resultElem <- searchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
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
