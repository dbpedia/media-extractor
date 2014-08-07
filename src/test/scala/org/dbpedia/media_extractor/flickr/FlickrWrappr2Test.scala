/**
 *
 */
package org.dbpedia.media_extractor.flickr

import org.scalatest.FunSpec

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 *
 */
class FlickrWrappr2Test extends FunSpec {

  describe("a FlickrWrappr2 instance") {

    val flickrWrappr2 = new FlickrWrappr2(
      serverRootUri = "http://localhost/flickrwrappr2/",
      flickrSavedCredentialsFile = "/flickr.setup.properties",
      flickrSavedAccessTokenFile = "/flickr.accessToken.properties")

    it("by default, it should lookup for Brussels (and within a radius of 5Km)") {
      assert(flickrWrappr2.flickrDBpediaLookup.targetResource === "Brussels")
      assert(flickrWrappr2.flickrGeoLookup.lat === "50.85")
      assert(flickrWrappr2.flickrGeoLookup.lon === "4.35")
      assert(flickrWrappr2.flickrGeoLookup.radius === flickrWrappr2.flickrGeoLookup.radius)
      assert(flickrWrappr2.flickrGeoLookup.radius === "5")
    }

    it("should be able to perform lookups with default parameters") {
      flickrWrappr2.flickrDBpediaLookup.performFlickrLookup()
      flickrWrappr2.flickrGeoLookup.performFlickrLookup()
    }

    describe("should be able to perform many lookups per time") {
      it("as DBpedia lookups") {
        flickrWrappr2.flickrDBpediaLookup.performFlickrLookup(targetResource = "La Paz", radius = "5")
        flickrWrappr2.flickrDBpediaLookup.performFlickrLookup(targetResource = "Buenos Aires", radius = "5")
      }
      it("as Geo lookups") {
        // La Paz
        flickrWrappr2.flickrGeoLookup.performFlickrLookup(lat = "-16.5", lon = "-68.15", radius = "5")
        flickrWrappr2.flickrGeoLookup.performFlickrLookup(lat = "-16.5", lon = "-68.1333", radius = "5")
        // Buenos Aires
        flickrWrappr2.flickrGeoLookup.performFlickrLookup(lat = "-34.6033", lon = "-58.3817", radius = "5")
      }
    }

  }

}