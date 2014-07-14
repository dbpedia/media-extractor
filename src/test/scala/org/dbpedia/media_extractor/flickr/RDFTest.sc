package org.dbpedia.media_extractor.flickr

import scala.xml._
import scala.io.Source._
import java.io._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary._

import com.hp.hpl.jena.shared._
import com.hp.hpl.jena.sparql.vocabulary._
import scala.collection.mutable

object RDFTest {

  val flickrXmlResponseFileString = getClass().getResourceAsStream("/flickr.photos.search.manneken_pis.response.xml")
  val flickrXmlResponse = XML.load(flickrXmlResponseFileString)

  val flickrSearchResultsList = FlickrWrappr2.generateUrisForFlickrSearchResponse(flickrXmlResponse)

  /* Initialize result model */

  var geoResultsModel = ModelFactory.createDefaultModel()

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

  geoResultsModel.setNsPrefix("foaf", foaf)
  geoResultsModel.setNsPrefix("dcterms", dcterms)
  geoResultsModel.setNsPrefix("rdfs", rdfs)
  //geoResultsModel.setNsPrefix("geonames", geonames)
  geoResultsModel.setNsPrefix("geo", geo)
  geoResultsModel.setNsPrefix("georss", georss)

  /* Perform flickr search */

  // (We already have a sample XML converted into "flickrSearchResultsList" :-)

  /* Important parameters */

  //Manneken Pis

  val lat = "50.845"
  val lon = "4.35"
  val radius = "5"

  val serverRootUri = "http://localhost/flickrwrappr/"
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"

  val geoPath = lat + "/" + lon + "/" + radius
  val locationFullUri = locationRootUri + geoPath
  val dataFullUri = dataRootUri + lat + geoPath

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"
  //val outputMode = "RDF/XML-ABBREV"

  /* Geo Search - Start */

  /* Process found photos */

  val locationFullUriResource = geoResultsModel.createResource(locationFullUri)

  for (resultElem <- flickrSearchResultsList) {
    val depictionUriResource = geoResultsModel.createResource(resultElem.depictionUri)
    val pageUriResource = geoResultsModel.createResource(resultElem.pageUri)

    locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
    depictionUriResource.addProperty(FOAF.page, pageUriResource)
  }

  /* Add metadata for location */

  val spatialThingResource = geoResultsModel.createResource(locationFullUri)
  spatialThingResource.addProperty(RDF.`type`, geo + "SpatialThing")

  val geoTypeProperty = geoResultsModel.createProperty("type", geo + "type")
  spatialThingResource.addProperty(geoTypeProperty, "SpatialThing")

  //TODO: make literals work
  //val latLiteral = geoResultsModel.createTypedLiteral(new Float(lat.toFloat))
  //val lonLiteral = geoResultsModel.createTypedLiteral(new Integer(lon.toInt))
  //  val radiusLiteral = geoResultsModel.createTypedLiteral(new Integer(radius.toInt))

  //val latProperty = spa
  //val geo_lat = geoResultsModel.add (geo + "lat")
  //  spatialThingResource.addProperty(geoLatProperty,lat)

  /* Add metadata for document */

  val foafDocumentResource = geoResultsModel.createResource(locationFullUri)
  foafDocumentResource.addProperty(RDF.`type`, foaf + "Document")

  val label = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " long=" + lon
  val labelLiteral = geoResultsModel.createLiteral(label, "en")
  foafDocumentResource.addProperty(RDFS.label, labelLiteral)
  foafDocumentResource.addProperty(FOAF.primaryTopic, locationFullUriResource)
  val flickrTOUResource = geoResultsModel.createResource(flickrTermsUri)
  foafDocumentResource.addProperty(DCTerms.license, flickrTOUResource)

  val dataFullUriResource = geoResultsModel.createResource(dataFullUri)
  dataFullUriResource.addProperty(RDFS.label, labelLiteral)

  val flickrwrapprLiteral = geoResultsModel.createLiteral(flickrwrappr, "en")
  val serverRootUriResource = geoResultsModel.createResource(serverRootUri)
  serverRootUriResource.addProperty(RDFS.label, flickrwrapprLiteral)

  val geoOutputXml = new FileOutputStream(myPath + "output.geo.xml")
  geoResultsModel.write(geoOutputXml, outputMode)

  /* Geo Search - End */

  /* DBpedia - Start */

  val searchText = "Manneken Pis"
  val dbpediaResourceUri = "http://dbpedia.org/resource/"
  val flickrwrapprPhotosUri = serverRootUri + "photos/"

  val dbpediaResourceFullUri = dbpediaResourceUri + searchText.trim.replaceAll(" ", "_").replaceAll("%2F", "/").replaceAll("%3A", ":")

  //TODO: Prepare and perform SPARQL query
  //TODO: For now, we use SPARQL query result from file "geo_coordinates_en.cropped.nt"

  // Initialize results model

  val dbpediaResultsModel = ModelFactory.createDefaultModel()

  dbpediaResultsModel.setNsPrefix("foaf", foaf)
  dbpediaResultsModel.setNsPrefix("dcterms", dcterms)
  dbpediaResultsModel.setNsPrefix("rdfs", rdfs)

  // Perform flickr search - (for now, we will use the results from flickrSearchResultsList :-)

  // Process flickr search results

  /* Process found photos */

  val dbpediaResourceFullUriResource = dbpediaResultsModel.createResource(dbpediaResourceFullUri)

  for (resultElem <- flickrSearchResultsList) {
    val depictionUriResource = dbpediaResultsModel.createResource(resultElem.depictionUri)
    val pageUriResource = dbpediaResultsModel.createResource(resultElem.pageUri)

    dbpediaResourceFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
    depictionUriResource.addProperty(FOAF.page, pageUriResource)
  }

  // Add metadata for document

  val foafDocumentResource2 = dbpediaResultsModel.createResource(dbpediaResourceFullUri)
  foafDocumentResource2.addProperty(RDF.`type`, foaf + "Document")

  val label2 = "Photos for Dbpedia resource " + searchText
  val labelLiteral2 = dbpediaResultsModel.createLiteral(label2, "en")
  foafDocumentResource2.addProperty(RDFS.label, labelLiteral2)

  foafDocumentResource2.addProperty(FOAF.primaryTopic, dbpediaResourceFullUriResource)

  val flickrTOUResource2 = dbpediaResultsModel.createResource(flickrTermsUri)
  foafDocumentResource2.addProperty(DCTerms.license, flickrTOUResource2)

  dataFullUriResource.addProperty(RDFS.label, labelLiteral2)


  val flickrwrapprLiteral2 = dbpediaResultsModel.createLiteral(flickrwrappr, "en")
  val serverRootUriResource2 = dbpediaResultsModel.createResource(serverRootUri)
  serverRootUriResource2.addProperty(RDFS.label, flickrwrapprLiteral2)


  val dbpediaOutputXml = new FileOutputStream(myPath + "output.dbpedia.xml")
  dbpediaResultsModel.write(dbpediaOutputXml, outputMode)

  /* DBpedia - End */






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
geoResultsModel.write(flickrGeoOUT, "N-TRIPLES")



val flickrDBpediaIN = new FileInputStream (myPath + "flickrwrappr.response.white_house.dbpedia.cropped.rdf.xml")
val flickrDBpediaModel = ModelFactory.createDefaultModel()
flickrDBpediaModel.read (flickrDBpediaIN, null, "RDF/XML")

val flickrDBpediaOUT = new FileOutputStream (myPath + "flickrwrappr.response.white_house.dbpedia.cropped.exported.nt")
flickrDBpediaModel.write(flickrDBpediaOUT, "N-TRIPLES")

*/
}