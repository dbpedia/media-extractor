package org.dbpedia.media_extractor.lookup_service

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.DCTerms
import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.search_result.SearchResult
import org.scribe.builder.api.Api
import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider

abstract class GeoLookupService[ProviderApi <: Api](
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  radius: String = "5",
  serviceProviderCallback: MediaLookupServiceProvider[ProviderApi])

  extends LookupService[ProviderApi](
    serviceProviderCallback,
    radius) {

  val geoPath = lat + "/" + lon + "/" + radius

  val dbpediaLocationRootUri = dbpediaRootUri + "location/"
  val dbpediaMediaLocationRootUri = dbpediaMediaRootUri + "location/"

  val dbpediaLocationFullUri = dbpediaLocationRootUri + geoPath
  val dbpediaMediaLocationFullUri = dbpediaMediaLocationRootUri + geoPath

  override protected val namespaceUriMap = super.namespaceUriMap ++ Map(
    //"geonames"-> "http://www.geonames.org/ontology#",
    "georss" -> "http://www.georss.org/georss/")

  def addSearchResultsToRDFGraph(searchResultsList: List[SearchResult], rdfGraph: Model)

  def addMetadataToRDFGraph(rdfGraph: Model) = {
    addLocationMetadataToRDFGraph(rdfGraph)
    addDocumentMetadataToRDFGraph(rdfGraph)
  }

  private def addLocationMetadataToRDFGraph(rdfGraph: Model) = {

    val latLiteral = rdfGraph.createTypedLiteral(lat.toFloat, "Float")
    val longLiteral = rdfGraph.createTypedLiteral(lon.toFloat, "Float")
    val radiusLiteral = rdfGraph.createTypedLiteral(radius.toDouble, "Double")

    val wgs84_posUri = namespaceUriMap("wgs84_pos")

    val latProperty = rdfGraph.createProperty(wgs84_posUri, "lat")
    val longProperty = rdfGraph.createProperty(wgs84_posUri, "long")
    val radiusProperty = rdfGraph.createProperty(wgs84_posUri, "radius")

    val typeProperty = rdfGraph.createProperty(wgs84_posUri, "type")

    val spatialThingResource = rdfGraph.createResource(dbpediaLocationFullUri)

    spatialThingResource.addProperty(latProperty, latLiteral)
    spatialThingResource.addProperty(longProperty, longLiteral)
    spatialThingResource.addProperty(radiusProperty, radiusLiteral)

    spatialThingResource.addProperty(RDF.`type`, wgs84_posUri + "SpatialThing")
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model) = {

    val lookupHeader = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon

    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")

    val foafUri = namespaceUriMap("foaf")

    val dbpediaLocationFullUriResource = rdfGraph.createResource(dbpediaLocationFullUri)
    val dbpediaMediaLocationFullUriResource = rdfGraph.createResource(dbpediaMediaLocationFullUri)
    val dbpediaMediaRootUriResource = rdfGraph.createResource(dbpediaMediaRootUri)
    val flickrTermsUriResource = rdfGraph.createResource(flickrTermsUri)

    dbpediaMediaRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
    dbpediaMediaLocationFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)
    dbpediaMediaLocationFullUriResource.addProperty(RDF.`type`, foafUri + "Document")
    dbpediaMediaLocationFullUriResource.addProperty(FOAF.primaryTopic, dbpediaLocationFullUriResource)
    dbpediaMediaLocationFullUriResource.addProperty(DCTerms.license, flickrTermsUriResource)
    dbpediaMediaLocationFullUriResource.addProperty(FOAF.maker, dbpediaMediaRootUriResource)
  }

  def performGeoLookup(lat: String = lat, lon: String = lon, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    val searchResults = serviceProviderCallback.oAuthSession.getSearchResults(serviceProviderCallback.oAuthSession.getSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, signRequest = signRequest))
    addSearchResultsToRDFGraph(searchResults, rdfGraph)

    rdfGraph
  }

}
