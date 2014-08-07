package org.dbpedia.media_extractor.flickr

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

case class FlickrGeoLookup(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  override val radius: String = "5",
  val locationRootUri: String,
  val dataRootUri: String,
  val serverRootUri: String,
  override val flickrOAuthSession: FlickrOAuthSession)

  extends FlickrLookup(flickrOAuthSession) {

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + geoPath

  override protected val namespacesMap = super.namespacesMap ++ Map(
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
    val spatialThingResource = rdfGraph.createResource(locationFullUri)
    spatialThingResource.addProperty(RDF.`type`, namespacesMap("wgs84_pos") + "SpatialThing")

    val wgs84_posTypeProperty = rdfGraph.createProperty("type", namespacesMap("wgs84_pos") + "type")
    spatialThingResource.addProperty(wgs84_posTypeProperty, "SpatialThing")

    val latFloat = lat.toFloat
    val lonFloat = lon.toFloat
    val radiusFloat = radius.toFloat

    val latLiteral = rdfGraph.createTypedLiteral(latFloat, "Float")
    val longLiteral = rdfGraph.createTypedLiteral(lonFloat, "Float")
    val radiusLiteral = rdfGraph.createTypedLiteral(radiusFloat, "Float")

    val wgs84_posLatUri = namespacesMap("wgs84_pos") + "lat"
    val wgs84_posLongUri = namespacesMap("wgs84_pos") + "long"
    val wgs84_posRadiusUri = namespacesMap("wgs84_pos") + "radius"

    val latProperty = rdfGraph.createProperty(wgs84_posLatUri, "lat")
    val longProperty = rdfGraph.createProperty(wgs84_posLongUri, "long")
    val radiusProperty = rdfGraph.createProperty(wgs84_posRadiusUri, "radius")

    spatialThingResource.addProperty(latProperty, latLiteral)
    spatialThingResource.addProperty(longProperty, longLiteral)
    spatialThingResource.addProperty(radiusProperty, radiusLiteral)
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model) = {
    val locationFullUriResource = rdfGraph.createResource(locationFullUri)

    val foafDocumentResource = rdfGraph.createResource(locationFullUri)
    foafDocumentResource.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val lookupHeader = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
    val lookupHeaderLiteral = rdfGraph.createLiteral(lookupHeader, "en")
    foafDocumentResource.addProperty(RDFS.label, lookupHeaderLiteral)
    foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
    val flickrTOUResource = rdfGraph.createResource(flickrTermsUri)
    foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

    val dataFullUriResource = rdfGraph.createResource(dataFullUri)
    dataFullUriResource.addProperty(RDFS.label, lookupHeaderLiteral)

    val lookupFooterLiteral = rdfGraph.createLiteral(lookupFooter, "en")
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    serverRootUriResource.addProperty(RDFS.label, lookupFooterLiteral)
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
