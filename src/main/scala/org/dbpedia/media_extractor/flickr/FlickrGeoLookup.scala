package org.dbpedia.media_extractor.flickr

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
    "geo" -> "http://www.w3.org/2003/01/geo/wgs84_pos#",
    "georss" -> "http://www.georss.org/georss/")

  def addFlickrSearchResultsToRDFGraph(rdfGraph: Model, flickrSearchResultsList: List[FlickrSearchResult], locationFullUriResource: Resource) = {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addMetadataToRDFGraph(rdfGraph: Model) = {
    val dataFullUriResource = rdfGraph.createResource(dataFullUri)

    addLocationMetadataToRDFGraph(rdfGraph)
    addDocumentMetadataToRDFGraph(rdfGraph, dataFullUriResource)
  }

  // FIXME: make literals work
  private def addLocationMetadataToRDFGraph(rdfGraph: Model) = {
    val spatialThingResource = rdfGraph.createResource(locationFullUri)
    spatialThingResource.addProperty(RDF.`type`, namespacesMap("geo") + "SpatialThing")

    val geoTypeProperty = rdfGraph.createProperty("type", namespacesMap("geo") + "type")
    spatialThingResource.addProperty(geoTypeProperty, "SpatialThing")

    // FIXME: make literals work
    //val latLiteral = geoResultsModel.createTypedLiteral(new Float(lat.toFloat))
    //val lonLiteral = geoResultsModel.createTypedLiteral(new Integer(lon.toInt))
    //val radiusLiteral = geoResultsModel.createTypedLiteral(new Integer(radius.toInt))

    //val latProperty = spa
    //val geo_lat = geoResultsModel.add (geo + "lat")
    //spatialThingResource.addProperty(geoLatProperty,lat)
  }

  private def addDocumentMetadataToRDFGraph(rdfGraph: Model, dataFullUriResource: Resource) = {
    val locationFullUriResource = rdfGraph.createResource(locationFullUri)

    val foafDocumentResource = rdfGraph.createResource(locationFullUri)
    foafDocumentResource.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val label = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
    val labelLiteral = rdfGraph.createLiteral(label, "en")
    foafDocumentResource.addProperty(RDFS.label, labelLiteral)
    foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
    val flickrTOUResource = rdfGraph.createResource(flickrTermsUri)
    foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

    dataFullUriResource.addProperty(RDFS.label, labelLiteral)

    val flickrwrapprLiteral = rdfGraph.createLiteral(lookupFooter, "en")
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    serverRootUriResource.addProperty(RDFS.label, flickrwrapprLiteral)
  }

  def performFlickrLookup(lat: String, lon: String, radius: String): Model = {
    val rdfGraph = ModelFactory.createDefaultModel()
    val locationFullUriResource = rdfGraph.createResource(locationFullUri)

    val flickrSearchResults = getFlickrSearchResults(getFlickrSearchResponse(searchText = "", latitude = lat, longitude = lon, radius, license, signRequest))

    addNameSpacesToRDFGraph(rdfGraph)
    addMetadataToRDFGraph(rdfGraph)
    addFlickrSearchResultsToRDFGraph(rdfGraph, flickrSearchResults, locationFullUriResource)

    rdfGraph
  }

}
