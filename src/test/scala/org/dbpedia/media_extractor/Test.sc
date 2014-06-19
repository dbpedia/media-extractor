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

  // Our model so far...
  resultsModel.write(System.out)

  /* Process found photos */

  val locationFullUriResource = resultsModel.createResource(locationFullUri)

  for (resultElem <- resultsList) {
    val depictionUriResource = resultsModel.createResource(resultElem.depictionUri)
    val pageUriResource = resultsModel.createResource(resultElem.pageUri)

    locationFullUriResource.addProperty(FOAF.depiction, depictionUriResource)
    depictionUriResource.addProperty(FOAF.page, pageUriResource)

    // I don't see the point in doing it this way
    /*
    val depictionUriProperty = resultsModel.createProperty(FOAF.depiction.toString, depictionUriResource.toString)
    val pageUriProperty = resultsModel.createProperty(FOAF.page.toString, pageUriResource.toString)
    
    resultsModel.add(locationFullUriResource, depictionUriProperty, depictionUriResource)
    */
  }

  /* Add metadata for location */
  //ModelFactory.create

  val spatialThingResource = resultsModel.createResource(locationFullUri)

  val rdfProperty = resultsModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

  //spatialThingResource.addProperty(resultsModel.getNsPrefixURI("geo"), "arg1", "arg2")

  /*
    
  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(locationUri),
    resultsModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
    resultsModel.createResource("http://www.w3.org/2003/01/geo/wgs84_pos#SpatialThing")))

  val latLiteral = resultsModel.createLiteral(lat, "http://www.w3.org/2001/XMLSchema#float")

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(locationUri),
    resultsModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat"),
    latLiteral))

  val longLiteral = resultsModel.createLiteral(lon, "http://www.w3.org/2001/XMLSchema#float")

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(locationUri),
    resultsModel.createProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long"),
    longLiteral))

  val radiusLiteral = resultsModel.createLiteral(radius, "http://www.w3.org/2001/XMLSchema#double")

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(locationUri),
    resultsModel.createProperty("http://www.georss.org/georss/radius"),
    radiusLiteral))


  /* Add metadata for document */

  val flickrWrapprHomepage = "http://www4.wiwiss.fu-berlin.de/flickrwrappr/"
  val flickrTosUri = "http://www.flickr.com/terms.gne"

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(dataUri),
    resultsModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
    resultsModel.createResource("http://xmlns.com/foaf/0.1/Document")))

  val resultsLabel = "Photos taken within " + radius + " meters of geographic location lat=" + lat + " lon=" + lon
  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(dataUri),
    resultsModel.createProperty("http://www.w3.org/2000/01/rdf-schema#label"),
    resultsModel.createLiteral(resultsLabel, "en")))

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(dataUri),
    resultsModel.createProperty("http://xmlns.com/foaf/0.1/primaryTopic"),
    resultsModel.createResource(locationUri)))

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(dataUri),
    resultsModel.createProperty("http://purl.org/dc/terms/license"),
    resultsModel.createResource(flickrTosUri)))

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(dataUri),
    resultsModel.createProperty("http://xmlns.com/foaf/0.1/maker"),
    resultsModel.createResource(flickrWrapprHomepage)))

  resultsModel.add(resultsModel.createStatement(resultsModel.createResource(flickrWrapprHomepage),
    resultsModel.createProperty("http://www.w3.org/2000/01/rdf-schema#label"),
    resultsModel.createLiteral("flickr(tm) wrappr", "en")))


val outputXml = new FileOutputStream(myPath + "output.xml")
  resultsModel.write(outputXml, "RDF/XML")

*/

  /*

  //var myFile = new File ("myRDF")
  //var out = new BufferedWriter(new FileWriter(myFile))
  //var writer = new Writer(
  // out.write(text);
  //out.close();
  //System.setOut(new PrintStream("myRDF"))
  // val file = Source.fro
  resultsModel.write(System.out, "N-TRIPLES")
  
  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  //var out = new FileOutputStream("myRDF.xml.abbrev.rdf")
  //resultsModel.write(out,"RDF/XML-ABBREV")
  //resultsModel.write(out,"RDF/XML-ABBREV")

  val inNT = new FileInputStream(myPath+"geo_coordinates_en.cropped.nt")
  var geoModel = ModelFactory.createDefaultModel
  geoModel.read(inNT, null, "N-TRIPLES")

var outXML = new FileOutputStream (myPath+"geo_coordinates_en.cropped.exported.plain.xml")
var outXMLABBREV = new FileOutputStream (myPath+"geo_coordinate_en.cropped.exported.abbrev.xml")

geoModel.write(outXML, "RDF/XML")
geoModel.write(outXMLABBREV, "RDF/XML-ABBREV")


*/

}