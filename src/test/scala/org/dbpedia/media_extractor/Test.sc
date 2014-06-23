package org.dbpedia.media_extractor.test

import scala.xml._
import scala.collection.mutable.ListBuffer
import scala.io.Source._
import java.io._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary._

import com.hp.hpl.jena.shared._
import com.hp.hpl.jena.sparql.vocabulary._
import scala.collection.mutable

object Test {

  val xmlString = Test.getClass().getResourceAsStream("/flickr.photos.search.manneken_pis.response.xml")

  val myXml = XML.load(xmlString)

  case class SearchResult(depictionUri: String, pageUri: String)

  val resultsListBuffer = new ListBuffer[SearchResult]

  (myXml \\ "rsp" \ "photos" \ "photo") foreach {
    photo =>
      //generate photo image URI
      val pictureUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
      //generate photo page URI
      val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

      resultsListBuffer += SearchResult(pictureUri, pageUri)
  }

  val resultsList = resultsListBuffer.toList

  /* Initialize result model */

  var resultsModel = ModelFactory.createDefaultModel()

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

  resultsModel.setNsPrefix("foaf", foaf)
  resultsModel.setNsPrefix("dcterms", dcterms)
  resultsModel.setNsPrefix("rdfs", rdfs)
  //resultsModel.setNsPrefix("geonames", geonames)
  resultsModel.setNsPrefix("geo", geo)
  resultsModel.setNsPrefix("georss", georss)

  /* Perform flickr search */

  // (We already have a sample XML converted into two lists :-)

  /* Important parameters */

  //Brussels

  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  val serverRootUri = "http://localhost/flickrwrappr/"
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + lat + geoPath

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  /* Process found photos */

  val locationFullUriResource = resultsModel.createResource(locationFullUri)

  for (resultElem <- resultsList) {
    val depictionUriResource = resultsModel.createResource(resultElem.depictionUri)
    val pageUriResource = resultsModel.createResource(resultElem.pageUri)

    locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
    depictionUriResource.addProperty(FOAF.page, pageUriResource)
  }


  /* Add metadata for location */

  val spatialThingResource = resultsModel.createResource(locationFullUri)
  spatialThingResource.addProperty(RDF.`type`, geo + "SpatialThing")

  val geoTypeProperty = resultsModel.createProperty("type", geo + "type")
  spatialThingResource.addProperty(geoTypeProperty, "SpatialThing")

  //TODO: make literals work
  //val latLiteral = resultsModel.createTypedLiteral(new Float(lat.toFloat))
  //val lonLiteral = resultsModel.createTypedLiteral(new Integer(lon.toInt))
  //  val radiusLiteral = resultsModel.createTypedLiteral(new Integer(radius.toInt))

  //val latProperty = spa
  //val geo_lat = resultsModel.add (geo + "lat")
  //  spatialThingResource.addProperty(geoLatProperty,lat)


  /* Add metadata for document */

  val foafDocumentResource = resultsModel.createResource(locationFullUri)
  foafDocumentResource.addProperty(RDF.`type`, foaf + "Document")

  val label = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
  val labelLiteral = resultsModel.createLiteral(label, "en")
  foafDocumentResource.addProperty(RDFS.label, labelLiteral)

  foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)

  val flickrTOUResource = resultsModel.createResource("http://www.flickr.com/terms.gne")
  foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

  val dataFullUriResource = resultsModel.createResource(dataFullUri)
  dataFullUriResource.addProperty(RDFS.label, labelLiteral)

  val flickrwrappr = "flickr(tm) wrappr"
  val flickrwrapprLiteral = resultsModel.createLiteral(flickrwrappr, "en")
  val serverRootUriResource = resultsModel.createResource(serverRootUri)
  serverRootUriResource.addProperty(RDFS.label, flickrwrapprLiteral)

  
  val outputXml = new FileOutputStream(myPath + "output.xml")
  resultsModel.write(outputXml, "RDF/XML-ABBREV")


  /* geo_coordiantes importing/exporting

  val inNT = new FileInputStream(myPath+"geo_coordinates_en.cropped.nt")
  var geoModel = ModelFactory.createDefaultModel
  geoModel.read(inNT, null, "N-TRIPLES")

var outXML = new FileOutputStream (myPath+"geo_coordinates_en.cropped.exported.plain.xml")
var outXMLABBREV = new FileOutputStream (myPath+"geo_coordinates_en.cropped.exported.abbrev.xml")

geoModel.write(outXML, "RDF/XML")
geoModel.write(outXMLABBREV, "RDF/XML-ABBREV")

*/

  /* flickrwrappr importing/exporting

val flickrGeoIN = new FileInputStream (myPath + "flickrwrappr.response.white_house.geo.cropped.rdf.xml")
val flickrGeoModel = ModelFactory.createDefaultModel()
flickrGeoModel.read (flickrGeoIN, null, "RDF/XML")


val flickrGeoOUT = new FileOutputStream (myPath + "flickrwrappr.response.white_house.geo.cropped.exported.nt")
resultsModel.write(flickrGeoOUT, "N-TRIPLES")



val flickrDBpediaIN = new FileInputStream (myPath + "flickrwrappr.response.white_house.dbpedia.cropped.rdf.xml")
val flickrDBpediaModel = ModelFactory.createDefaultModel()
flickrDBpediaModel.read (flickrDBpediaIN, null, "RDF/XML")

val flickrDBpediaOUT = new FileOutputStream (myPath + "flickrwrappr.response.white_house.dbpedia.cropped.exported.nt")
flickrDBpediaModel.write(flickrDBpediaOUT, "N-TRIPLES")

*/
}