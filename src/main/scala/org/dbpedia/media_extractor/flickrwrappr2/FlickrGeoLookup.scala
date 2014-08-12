package org.dbpedia.media_extractor.flickrwrappr2

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

case class FlickrGeoLookup(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  override val radius: String = "5",
  val serverRootUri: String,
  override val flickrOAuthSession: FlickrOAuthSession)

  extends FlickrLookup(flickrOAuthSession) {

  val geoPath = lat + "/" + lon + "/" + radius
  val locationRootUri = serverRootUri + "location/"

  val dataPhotosDepictingLocationRootUri = serverRootUri + "data/photosDepictingLocation/"

  val locationFullUri = locationRootUri + geoPath
  val dataPhotosDepictingLocationFullUri = dataPhotosDepictingLocationRootUri + geoPath

  override protected val namespaceUriMap = super.namespaceUriMap ++ Map(
    //"geonames"-> "http://www.geonames.org/ontology#",
    "georss" -> "http://www.georss.org/georss/")

  def addFlickrSearchResultsToRDFGraph(flickrSearchResultsList: List[FlickrSearchResult], rdfGraph: Model) = {
    val locationFullUriResource = rdfGraph.createResource(locationFullUri)
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
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

    val spatialThingResource = rdfGraph.createResource(locationFullUri)

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

    val locationFullUriResource = rdfGraph.createResource(locationFullUri)
    val dataPhotosDepictingLocationFullUriResource = rdfGraph.createResource(dataPhotosDepictingLocationFullUri)
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    val flickrTermsUriResource = rdfGraph.createResource(flickrTermsUri)

    serverRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
    dataPhotosDepictingLocationFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)
    dataPhotosDepictingLocationFullUriResource.addProperty(RDF.`type`, foafUri + "Document")
    dataPhotosDepictingLocationFullUriResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
    dataPhotosDepictingLocationFullUriResource.addProperty(DCTerms.license, flickrTermsUriResource)
    dataPhotosDepictingLocationFullUriResource.addProperty(FOAF.maker, serverRootUriResource)
  }

  def performFlickrLookup(lat: String = lat, lon: String = lon, radius: String = radius): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)

    val flickrSearchResults = getFlickrSearchResults(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, license, signRequest))
    addFlickrSearchResultsToRDFGraph(flickrSearchResults, rdfGraph)

    rdfGraph
  }

}
