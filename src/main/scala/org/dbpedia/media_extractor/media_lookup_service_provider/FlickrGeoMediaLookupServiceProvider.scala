package org.dbpedia.media_extractor.media_lookup_service_provider

import com.hp.hpl.jena.rdf.model.ModelFactory
import org.dbpedia.media_extractor.search_result.FlickrSearchResult
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.scribe.builder.api.FlickrApi

class FlickrGeoMediaLookupServiceProvider[FlickrApi](
  val targetLicenses: String,
  val oAuthSession: OAuthSession[FlickrApi],
  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5")

  extends FlickrMediaLookupServiceProvider {

  override def performGeoLookup(lat: String = lat, lon: String = lon, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    semanticLookupService.addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    val flickrSearchResults = getSearchResults(oAuthSession.getFlickrSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, license, signRequest))
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
