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

}
