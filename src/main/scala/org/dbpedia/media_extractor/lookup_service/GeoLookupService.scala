package org.dbpedia.media_extractor.media_provider

import com.hp.hpl.jena.rdf.model.Model

abstract class GeoLookupService(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  radius: String = "5",
  val mediaProviderOAuthSession: MediaProviderOAuthSession)

  extends LookupService(mediaProviderOAuthSession, radius) {
  // TODO: complete this empty stub

  def performGeoLookup(lat: String = "50.85", lon: String = "4.35", radius: String = radius): Model

  val geoPath = lat + "/" + lon + "/" + radius

  val dbpediaLocationRootUri = dbpediaRootUri + "location/"
  val dbpediaMediaLocationRootUri = dbpediaMediaRootUri + "location/"

  val dbpediaLocationFullUri = dbpediaLocationRootUri + geoPath
  val dbpediaMediaLocationFullUri = dbpediaMediaLocationRootUri + geoPath

  override protected val namespaceUriMap = super.namespaceUriMap ++ Map(
    //"geonames"-> "http://www.geonames.org/ontology#",
    "georss" -> "http://www.georss.org/georss/")

  def addSearchResultsToRDFGraph(searchResultsList: List[FlickrSearchResult], rdfGraph: Model) = {
    val dbpediaMediaLocationFullUriResource = rdfGraph.createResource(dbpediaMediaLocationFullUri)
    for (resultElem <- searchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      dbpediaMediaLocationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

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

    val flickrSearchResults = getSearchResults(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, license, signRequest))
    addSearchResultsToRDFGraph(flickrSearchResults, rdfGraph)

    rdfGraph
  }
}
