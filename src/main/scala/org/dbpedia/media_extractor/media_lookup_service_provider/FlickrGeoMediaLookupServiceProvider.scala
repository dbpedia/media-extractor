package org.dbpedia.media_extractor.media_lookup_service_provider

import org.dbpedia.media_extractor.media_provider_session.FlickrMediaProviderOAuthSession

class FlickrGeoMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: FlickrMediaProviderOAuthSession,
  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5")

  extends FlickrMediaLookupServiceProvider {

  //TODO: complete stub

  override def performGeoLookup(lat: String = lat, lon: String = lon, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    val flickrSearchResults = getSearchResults(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, license, signRequest))
    addSearchResultsToRDFGraph(flickrSearchResults, rdfGraph)

    rdfGraph
  }

  def addSearchResultsToRDFGraph(searchResultsList: List[FlickrSearchResult], rdfGraph: Model) = {
    val dbpediaMediaLocationFullUriResource = rdfGraph.createResource(dbpediaMediaLocationFullUri)
    for (resultElem <- searchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      dbpediaMediaLocationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }
}
