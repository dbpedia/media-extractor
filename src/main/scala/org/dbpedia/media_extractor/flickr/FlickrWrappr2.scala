package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import com.hp.hpl.jena.vocabulary.DCTerms

case class SearchResult(depictionUri: String, pageUri: String)

class FlickrWrappr2(val serverRootUri: String) {
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"

  // By default, search for Brussels
  val searchText = "Brussels"
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  val geoPath = lat + "/" + lon + "/" + radius

  val locationFullUri = locationRootUri + geoPath
  val locationFullUriResource = FlickrWrappr2.geoRDFGraph.createResource(locationFullUri)

  val dataFullUri = dataRootUri + geoPath

  val dataFullUriResource = FlickrWrappr2.geoRDFGraph.createResource(dataFullUri)

  val dbpediaResourceFullUri = FlickrWrappr2.dbpediaResourceUri + searchText.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")
  val dbpediaResourceFullUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(dbpediaResourceFullUri)

  // FIXME: make literals work
  def addGeoSearchLocationMetadataToGeoRDFGraph {
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

  def addGeoSearchDocumentMetadataToGeoRDFGraph {
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

  def addDBpediaSearchDocumentMetadataToDBpediaRDFGraph {
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

object FlickrWrappr2 extends App {

  val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")

  val dbpediaResourceUri = "http://dbpedia.org/resource/"

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

  val geoRDFGraph = ModelFactory.createDefaultModel()
  val dbpediaRDFGraph = ModelFactory.createDefaultModel()

  // Namespaces
  val foaf = "http://xmlns.com/foaf/0.1/"
  val dcterms = "http://purl.org/dc/terms/"
  val rdfs = "http://www.w3.org/2000/01/rdf-schema#"
  //val geonames = "http://www.geonames.org/ontology#"
  val geo = "http://www.w3.org/2003/01/geo/wgs84_pos#"
  val georss = "http://www.georss.org/georss/"

  // Auto-generated Namespaces
  val rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  val xsd = "http://www.w3.org/2001/XMLSchema#"
  val owl = "http://www.w3.org/2002/07/owl#"
  val dc = "http://purl.org/dc/elements/1.1/"
  val vcard = "http://www.w3.org/2001/vcard-rdf/3.0#"

  val commonNamespacesMap = Map("foaf" -> foaf,
    "dcterms" -> dcterms,
    "rdfs" -> rdfs)

  val geoNamespacesMap = Map( //"geonames"-> geonames,
    "geo" -> geo,
    "georss" -> georss)

  def apply(serverRootUri: String = "http://localhost/flickrwrappr/") = new FlickrWrappr2(serverRootUri)

  def generateUrisForFlickrSearchResponse(myXml: Elem): List[SearchResult] = {
    val resultsListBuffer = new ListBuffer[SearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += SearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def addNameSpacesToRDFGraph(nsMap: Map[String, String], rdfGraph: Model) =
    nsMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

  def addNameSpacesToGeoSearchRDFGraph() = {
    addNameSpacesToRDFGraph(commonNamespacesMap, geoRDFGraph)
    addNameSpacesToRDFGraph(geoNamespacesMap, geoRDFGraph)
  }

  def addNameSpacesToDBpediaSearchRDFGraph() =
    addNameSpacesToRDFGraph(commonNamespacesMap, dbpediaRDFGraph)

  def addFlickrGeoSearchResultsToGeoSearchRDFGraph(flickrSearchResultsList: List[SearchResult]) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = geoRDFGraph.createResource(resultElem.depictionUri)
      val pageUriResource = geoRDFGraph.createResource(resultElem.pageUri)
      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def addFlickrDBpediaSearchResultsToDBpediaSearchRDFGraph(flickrSearchResultsList: List[SearchResult], targetResource: String) {
    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(resultElem.depictionUri)
      val pageUriResource = FlickrWrappr2.dbpediaRDFGraph.createResource(resultElem.pageUri)
      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }
  def performFlickrGeoSearch(latitude: String = lat, longitude: String = lon, searchRadius: String = radius) {
  }

  // TODO: implement stub
  def performFlickrDBpediaSearch(targetResource: String, searchRadius: String = radius) {
  }

}

