/**
 *
 */
package org.dbpedia.media_extractor.flickr

import java.io.FileOutputStream
import org.scalatest.FunSpec

/**
 * @author allentiak
 *
 */
class FlickrLookupTest extends FunSpec {

  describe("a FlickrGeoLookup instance") {
    describe("should generate an RDF graph") {

      // Geo default parameters:
      val lat = "50.85"
      val lon = "4.35"
      val radius = "5"
      val serverRootUri: String = "http://localhost/flickrwrappr/"
      val locationRootUri = serverRootUri + "location/"
      val dataRootUri = serverRootUri + "data/photosDepictingLocation/"

      val flickrOAuthSession = FlickrOAuthSession(credentialsFile = "/flickr.setup.properties", accessTokenFile = "/flickr.accessToken.properties")

      //val flickrWrappr2 = FlickrWrappr2()

      val flickrGeoLookup = new FlickrGeoLookup(
        lat = lat,
        lon = lon,
        radius = radius,
        locationRootUri = locationRootUri,
        dataRootUri = dataRootUri,
        serverRootUri = serverRootUri,
        flickrOAuthSession = flickrOAuthSession)

      val myRDFGraph = flickrGeoLookup.performFlickrLookup(lat, lon, radius)

      val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

      val geoOutputXml = new FileOutputStream(myPath + "FlickrWrappr2Test.output.geo.xml")
      myRDFGraph.write(geoOutputXml, "RDF/XML")

      val geoOutputNt = new FileOutputStream(myPath + "FlickrWrappr2Test.output.geo.nt")
      myRDFGraph.write(geoOutputNt, "N-TRIPLES")

    }
  }

  describe("a FlickrDBpediaLookup instance") {
    describe("should generate an RDF graph")(pending)
  }
}
