package org.dbpedia.media_extractor.flickr

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.DCTerms
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

case class FlickrSearchResult(depictionUri: String, pageUri: String)

trait FlickrSearch {

  protected val namespacesMap = Map(
    "foaf" -> "http://xmlns.com/foaf/0.1/",
    "dcterms" -> "http://purl.org/dc/terms/",
    "rdfs" -> "http://www.w3.org/2000/01/rdf-schema#")

  val rdfGraph = ModelFactory.createDefaultModel()

  val license = "1,2"
  val radius = "5"

  protected def addNameSpacesToRDFGraphFromMap(nsMap: Map[String, String]) =
    nsMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

  def addMetadataToRDFGraph() = ???

  def addNameSpacesToRDFGraph() = addNameSpacesToRDFGraphFromMap(namespacesMap)

  def addFlickrSearchResultsToRDFGraph(flickrSearchResultsList: List[FlickrSearchResult]) = ???
}

// By default, search for Brussels
case class FlickrGeoSearch(

  val lat: String = "50.85",
  val lon: String = "4.35",
  radius: String = "5",
  val locationRootUri: String,
  val dataRootUri: String,
  val serverRootUri: String)

  extends FlickrSearch {

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + geoPath

  val dataFullUriResource = rdfGraph.createResource(dataFullUri)
  val locationFullUriResource = rdfGraph.createResource(locationFullUri)

  override protected val namespacesMap = super.namespacesMap ++ Map(
    //"geonames"-> "http://www.geonames.org/ontology#",
    "geo" -> "http://www.w3.org/2003/01/geo/wgs84_pos#",
    "georss" -> "http://www.georss.org/georss/")

  def addFlickrSearchResultsToRDFGraph(flickrSearchResultsList: List[FlickrSearchResult]) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addMetadataToRDFGraph() {
    addLocationMetadataToRDFGraph()
    addDocumentMetadataToRDFGraph()
  }

  // FIXME: make literals work
  private def addLocationMetadataToRDFGraph() = {
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

  private def addDocumentMetadataToRDFGraph() = {
    val locationFullUriResource = rdfGraph.createResource(locationFullUri)

    val foafDocumentResource = rdfGraph.createResource(locationFullUri)
    foafDocumentResource.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val label = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
    val labelLiteral = rdfGraph.createLiteral(label, "en")
    foafDocumentResource.addProperty(RDFS.label, labelLiteral)
    foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
    val flickrTOUResource = rdfGraph.createResource(FlickrWrappr2.flickrTermsUri)
    foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

    dataFullUriResource.addProperty(RDFS.label, labelLiteral)

    val flickrwrapprLiteral = rdfGraph.createLiteral(FlickrWrappr2.flickrwrappr, "en")
    val serverRootUriResource = rdfGraph.createResource(serverRootUri)
    serverRootUriResource.addProperty(RDFS.label, flickrwrapprLiteral)
  }

}

// By default, search for Brussels
case class FlickrDBpediaSearch(

  val targetResource: String = "Brussels",
  val serverRootUri: String)

  extends FlickrSearch {

  val dbpediaResourceUri = "http://dbpedia.org/resource/"
  val dbpediaResourceFullUri = dbpediaResourceUri + targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  val dbpediaResourceFullUriResource = rdfGraph.createResource(dbpediaResourceFullUri)

  def addFlickrSearchResultsToRDFGraph(flickrSearchResultsList: List[FlickrSearchResult]) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addNameSpacesToDBpediaSearchRDFGraph() =
    super.addNameSpacesToRDFGraphFromMap(namespacesMap)

  def addMetadataToRDFGraph() {
    addDocumentMetadataToRDFGraph()
  }

  private def addDocumentMetadataToRDFGraph() = {
    val foafDocumentResource2 = rdfGraph.createResource(dbpediaResourceFullUri)
    foafDocumentResource2.addProperty(RDF.`type`, namespacesMap("foaf") + "Document")

    val label2 = "Photos for Dbpedia resource " + targetResource
    val labelLiteral2 = rdfGraph.createLiteral(label2, "en")
    foafDocumentResource2.addProperty(RDFS.label, labelLiteral2)

    foafDocumentResource2.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)

    val flickrTOUResource2 = rdfGraph.createResource(FlickrWrappr2.flickrTermsUri)
    foafDocumentResource2.addProperty(DCTerms.license, flickrTOUResource2)

    dbpediaResourceFullUriResource.addProperty(RDFS.label, labelLiteral2)

    val flickrwrapprLiteral2 = rdfGraph.createLiteral(FlickrWrappr2.flickrwrappr, "en")
    val serverRootUriResource2 = rdfGraph.createResource(serverRootUri)
    serverRootUriResource2.addProperty(RDFS.label, flickrwrapprLiteral2)
  }

}
