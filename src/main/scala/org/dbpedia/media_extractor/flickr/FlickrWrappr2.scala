package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.RDF

case class SearchResult(depictionUri: String, pageUri: String)

class FlickrWrappr2(val serverRootUri: String)

object FlickrWrappr2 extends App {
  val geoRDFGraph = ModelFactory.createDefaultModel()
  val dbpediaRDFGraph = ModelFactory.createDefaultModel()

  val dbpediaResourceUri = "http://dbpedia.org/resource/"

  val defaultServerRootUri = "http://localhost/flickrwrappr/"

  val locationRootUri = defaultServerRootUri + "location/"
  val dataRootUri = defaultServerRootUri + "data/photosDepictingLocation/"

  // By default, search for Brussels
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + lat + geoPath

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

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

  def apply(serverRootUri: String = defaultServerRootUri) = new FlickrWrappr2(serverRootUri)

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

  // TODO: implement stub 
  def performFlickrGeoSearch(latitude: String = lat, longitude: String = lon, searchRadius: String = radius) {
  }

  // TODO: implement stub
  def performFlickrDBpediaSearch(targetResource: String, searchRadius: String = radius) {
  }

  def processFlickrGeoSearchResults(flickrSearchResultsList: List[SearchResult]) {
    val locationFullUriResource = geoRDFGraph.createResource(locationFullUri)

    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = geoRDFGraph.createResource(resultElem.depictionUri)
      val pageUriResource = geoRDFGraph.createResource(resultElem.pageUri)

      locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  def processFlickrDBpediaSearchResults(flickrSearchResultsList: List[SearchResult], targetResource: String) {
    val dbpediaResourceFullUri = FlickrWrappr2.dbpediaResourceUri + targetResource.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

    val dbpediaResourceFullUriResource = dbpediaRDFGraph.createResource(dbpediaResourceFullUri)

    for (resultElem <- flickrSearchResultsList) {
      val depictionUriResource = dbpediaRDFGraph.createResource(resultElem.depictionUri)
      val pageUriResource = dbpediaRDFGraph.createResource(resultElem.pageUri)

      dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
      depictionUriResource.addProperty(FOAF.page, pageUriResource)
    }
  }

  // FIXME: make literals work
  def addGeoLocationMetadataToRDFGraph {
    val spatialThingResource = geoRDFGraph.createResource(locationFullUri)
    spatialThingResource.addProperty(RDF.`type`, geo + "SpatialThing")

    val geoTypeProperty = geoRDFGraph.createProperty("type", geo + "type")
    spatialThingResource.addProperty(geoTypeProperty, "SpatialThing")

    // FIXME: make literals work
    //val latLiteral = geoResultsModel.createTypedLiteral(new Float(lat.toFloat))
    //val lonLiteral = geoResultsModel.createTypedLiteral(new Integer(lon.toInt))
    //val radiusLiteral = geoResultsModel.createTypedLiteral(new Integer(radius.toInt))

    //val latProperty = spa
    //val geo_lat = geoResultsModel.add (geo + "lat")
    //spatialThingResource.addProperty(geoLatProperty,lat)
  }

  // TODO: implement stub
  def addGeoSearchDocumentMetadataToRDFGraph

  // TODO: implement stub
  def addDBpediaSearchDocumentMetadataToRDFGraph
}

