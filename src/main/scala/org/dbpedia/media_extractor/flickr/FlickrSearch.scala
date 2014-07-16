package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.DCTerms

case class FlickrSearchResult(depictionUri: String, pageUri: String)

class FlickrSearch {
  // Namespaces
  protected val foaf = "http://xmlns.com/foaf/0.1/"
  protected val dcterms = "http://purl.org/dc/terms/"
  protected val rdfs = "http://www.w3.org/2000/01/rdf-schema#"
  //protected val geonames = "http://www.geonames.org/ontology#"
  protected val geo = "http://www.w3.org/2003/01/geo/wgs84_pos#"
  protected val georss = "http://www.georss.org/georss/"

  // Auto-generated Namespaces
  protected val rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  protected val xsd = "http://www.w3.org/2001/XMLSchema#"
  protected val owl = "http://www.w3.org/2002/07/owl#"
  protected val dc = "http://purl.org/dc/elements/1.1/"
  protected val vcard = "http://www.w3.org/2001/vcard-rdf/3.0#"

  protected val commonNamespacesMap = Map("foaf" -> foaf,
    "dcterms" -> dcterms,
    "rdfs" -> rdfs)

  val rdfGraph: Model

  def addNameSpacesToRDFGraph(nsMap: Map[String, String]) =
    nsMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

  def addMetadataToRDFGraph() = ???
}

// By default, search for Brussels
case class FlickrGeoSearch(

  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5",
  val locationRootUri: String,
  val dataRootUri: String,
  val serverRootUri: String)

  extends FlickrSearch {

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + geoPath

  val dataFullUriResource = FlickrWrappr2.geoRDFGraph.createResource(dataFullUri)
  val locationFullUriResource = FlickrWrappr2.geoRDFGraph.createResource(locationFullUri)

  private val geoNamespacesMap = Map( //"geonames"-> geonames,
    "geo" -> geo,
    "georss" -> georss)

  def addMetadataToRDFGraph() {
    addLocationMetadataToRDFGraph()
    addDocumentMetadataToRDFGraph()
  }

  def addNameSpacesToRDFGraph() = {
    super.addNameSpacesToRDFGraph(commonNamespacesMap)
    super.addNameSpacesToRDFGraph(geoNamespacesMap)
  }

  def addFlickrGeoSearchResultsToGeoSearchRDFGraph(flickrSearchResultsList: List[FlickrSearchResult]) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = rdfGraph.createResource(resultElem.depictionUri)
      val pageUriResource = rdfGraph.createResource(resultElem.pageUri)
      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  // FIXME: make literals work
  private def addLocationMetadataToRDFGraph() = {
    val spatialThingResource = FlickrWrappr2.geoRDFGraph.createResource(locationFullUri)
    spatialThingResource.addProperty(RDF.`type`, FlickrWrappr2.geo + "SpatialThing")

    val geoTypeProperty = FlickrWrappr2.geoRDFGraph.createProperty("type", FlickrWrappr2.geo + "type")
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
    val locationFullUriResource = FlickrWrappr2.geoRDFGraph.createResource(locationFullUri)

    val foafDocumentResource = FlickrWrappr2.geoRDFGraph.createResource(locationFullUri)
    foafDocumentResource.addProperty(RDF.`type`, FlickrWrappr2.foaf + "Document")

    val label = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
    val labelLiteral = FlickrWrappr2.geoRDFGraph.createLiteral(label, "en")
    foafDocumentResource.addProperty(RDFS.label, labelLiteral)
    foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
    val flickrTOUResource = FlickrWrappr2.geoRDFGraph.createResource(FlickrWrappr2.flickrTermsUri)
    foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

    dataFullUriResource.addProperty(RDFS.label, labelLiteral)

    val flickrwrapprLiteral = FlickrWrappr2.geoRDFGraph.createLiteral(FlickrWrappr2.flickrwrappr, "en")
    val serverRootUriResource = FlickrWrappr2.geoRDFGraph.createResource(serverRootUri)
    serverRootUriResource.addProperty(RDFS.label, flickrwrapprLiteral)
  }

}

// By default, search for Brussels
case class FlickrDBpediaSearch(

  val searchText: String = "Brussels")

  extends FlickrSearch {

  val dbpediaResourceUri = "http://dbpedia.org/resource/"
  val dbpediaResourceFullUri = dbpediaResourceUri + searchText.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  val dbpediaResourceFullUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(dbpediaResourceFullUri)

  def addFlickrDBpediaSearchResultsToDBpediaSearchRDFGraph(flickrSearchResultsList: List[FlickrSearchResult], targetResource: String) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(resultElem.depictionUri)
      val pageUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(resultElem.pageUri)
      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addNameSpacesToDBpediaSearchRDFGraph() =
    super.addNameSpacesToRDFGraph(commonNamespacesMap)

  def addMetadataToRDFGraph() {
    addDocumentMetadataToRDFGraph()
  }

  private def addDocumentMetadataToRDFGraph = {
    val foafDocumentResource2 = FlickrWrappr2.dbpediaRDFGraph.createResource(dbpediaResourceFullUri)
    foafDocumentResource2.addProperty(RDF.`type`, FlickrWrappr2.foaf + "Document")

    val label2 = "Photos for Dbpedia resource " + searchText
    val labelLiteral2 = FlickrWrappr2.dbpediaRDFGraph.createLiteral(label2, "en")
    foafDocumentResource2.addProperty(RDFS.label, labelLiteral2)

    foafDocumentResource2.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)

    val flickrTOUResource2 = FlickrWrappr2.dbpediaRDFGraph.createResource(FlickrWrappr2.flickrTermsUri)
    foafDocumentResource2.addProperty(DCTerms.license, flickrTOUResource2)

    dataFullUriResource.addProperty(RDFS.label, labelLiteral2)

    val flickrwrapprLiteral2 = FlickrWrappr2.dbpediaRDFGraph.createLiteral(FlickrWrappr2.flickrwrappr, "en")
    val serverRootUriResource2 = FlickrWrappr2.dbpediaRDFGraph.createResource(serverRootUri)
    serverRootUriResource2.addProperty(RDFS.label, flickrwrapprLiteral2)
  }

}
