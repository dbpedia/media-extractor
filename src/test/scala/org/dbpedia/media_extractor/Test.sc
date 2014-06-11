package org.dbpedia.media_extractor.test

import scala.xml._

import scala.collection.mutable.ListBuffer

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary._
import com.hp.hpl.jena.shared._

object Test {

  val xmlString = Test.getClass().getResourceAsStream("/flickr.photos.search.manneken_pis.response.xml")

  /*
Respose XML:

<?xml version="1.0" encoding="utf-8" ?>
<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
  <photos page="1" pages="193" perpage="10" total="1927">
    <photo id="2934609620" owner="13808372@N04" secret="ee0f89aa66" server="3177" farm="4" title="Manneken Pis" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="7074274419" owner="25659362@N00" secret="13c28d0cca" server="5328" farm="6" title="Manneken Pis" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322858611" owner="25221135@N08" secret="2abdc8b30c" server="8218" farm="9" title="Manneken-Pis Corrida 33" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322860021" owner="25221135@N08" secret="e99c1ba6ef" server="8212" farm="9" title="Manneken-Pis Corrida 36" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322870253" owner="25221135@N08" secret="6bf18c10fd" server="8361" farm="9" title="Manneken-Pis Corrida 56" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322855813" owner="25221135@N08" secret="a81706fdcf" server="8219" farm="9" title="Manneken-Pis Corrida 27" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322874345" owner="25221135@N08" secret="87e2a1c00b" server="8361" farm="9" title="Manneken-Pis Corrida 58" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8322867775" owner="25221135@N08" secret="57a40e9a7d" server="8358" farm="9" title="Manneken-Pis Corrida 51" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8323925784" owner="25221135@N08" secret="1e4b24fb5a" server="8213" farm="9" title="Manneken-Pis Corrida 48" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="8323917472" owner="25221135@N08" secret="cf879f260e" server="8075" farm="9" title="Manneken-Pis Corrida 32" ispublic="1" isfriend="0" isfamily="0" />
  </photos>
</rsp>

*/

  val myXml = XML.load(xmlString)

  println((myXml \\ "rsp" \ "photos" \ "photo")(0))

  val pictureURIsBuffer = new ListBuffer[String]
  val pageURIsBuffer = new ListBuffer[String]
  (myXml \\ "rsp" \ "photos" \ "photo") foreach {
    photo =>
      //generate photo image URI
      pictureURIsBuffer += "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
      //generate photo page URI
      pageURIsBuffer += "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")
  }

  val pictureURIsList = pictureURIsBuffer.toList
  val pageURIsList = pageURIsBuffer.toList

  /* Initialize result model */

  val resultsModel = ModelFactory.createDefaultModel()

  resultsModel.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/")
  resultsModel.setNsPrefix("dcterms", "http://purl.org/dc/terms/")
  resultsModel.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#")
  //resultsModel.setNsPrefix("geonames", "http://www.geonames.org/ontology#")
  resultsModel.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#")
  resultsModel.setNsPrefix("georss", "http://www.georss.org/georss/")

  /* Perform flickr search */

  // (We already have a sample XML converted into two lists :-)



/* Important parameters */

  //Brussels
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  val uriRoot = "http://localhost/flickrwrappr/"
  val locationUriRoot = uriRoot + "location/"
  val dataUriRoot = uriRoot + "data/photosDepictingLocation/"

  val geoPath = lat + "/" + lon + "/" + radius
  val locationUri = locationUriRoot + geoPath
  val dataUri = dataUriRoot + lat + geoPath


/* Process found photos */

  for (pictureUri <- pictureURIsList; pageUri <- pageURIsList) {
    //provide photo picture uri
    resultsModel.add(resultsModel.createStatement(resultsModel.createResource(locationUri),
      resultsModel.createProperty("http://xmlns.com/foaf/0.1/depiction"),
      resultsModel.createResource(pictureUri)))
    //provide photo page uri
    resultsModel.add(resultsModel.createStatement(resultsModel.createResource(pictureUri),
      resultsModel.createProperty("http://xmlns.com/foaf/0.1/page"),
      resultsModel.createResource(pageUri)))
  }


	    /* Add metadata for location */

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

}

