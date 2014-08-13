package org.dbpedia.media_extractor.flickrwrappr2

import java.io.FileOutputStream

import org.scalatest.FunSpec

class FlickrLookupTest extends FunSpec {
  
  describe("FlickrLookupTest") {

    val flickrOAuthSession = FlickrOAuthSession(credentialsFile = "/flickr.setup.properties", accessTokenFile = "/flickr.accessToken.properties")
    val serverRootUri = "http://localhost/flickrwrappr/"
    val localPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"
    val radius = "5"

    describe("a FlickrGeoLookup instance") {

      describe("should generate an RDF graph") {

        // GeoLookup for Brussels
        val lat = "50.85"
        val lon = "4.35"

        val locationRootUri = serverRootUri + "location/"
        val dataRootUri = serverRootUri + "data/photosDepictingLocation/"

        val flickrGeoLookup = new FlickrGeoLookup(
          lat,
          lon,
          radius,
          serverRootUri,
          flickrOAuthSession)

        val geoLookupRdfGraph = flickrGeoLookup.performFlickrLookup(lat, lon, radius)

        val geoLookupXmlOutputStream = new FileOutputStream(localPath + "FlickrLookupTest.output.geo.xml")
        geoLookupRdfGraph.write(geoLookupXmlOutputStream, "RDF/XML")

        val geoNtOutputStream = new FileOutputStream(localPath + "FlickrLookupTest.output.geo.nt")
        geoLookupRdfGraph.write(geoNtOutputStream, "N-TRIPLES")

      }
    }

    describe("a FlickrDBpediaLookup instance") {

      describe("should generate an RDF graph") {

        val targetResource = "Brussels"

        val flickrDBpediaLookup = new FlickrDBpediaLookup(
          targetResource,
          serverRootUri,
          flickrOAuthSession)

        val dbpediaRdfGraph = flickrDBpediaLookup.performFlickrLookup(targetResource, radius)

        val dbpediaXmlOutputStream = new FileOutputStream(localPath + "FlickrLookupTest.output.dbpedia.xml")
        dbpediaRdfGraph.write(dbpediaXmlOutputStream, "RDF/XML")

        val dbpediaNtOutputStream = new FileOutputStream(localPath + "FlickrLookupTest.output.dbpedia.nt")
        dbpediaRdfGraph.write(dbpediaNtOutputStream, "N-TRIPLES")
      }
    }

  }
}
