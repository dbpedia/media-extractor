/**
 *
 */
package org.dbpedia.media_extractor.flickr

/**
 * @author allentiak
 *
 */
class FlickrWrappr2Test extends FunSpec {

  describe("a FlickrWrappr2 instance") {
    describe("should generate RDF graphs") {
      describe("for Geo Search") {
        val flickrGeoLookup: FlickrLookup = FlickrGeoLookup(
          lat = lat,
          lon = lon,
          radius = radius,
          locationRootUri = locationRootUri,
          dataRootUri = dataRootUri,
          serverRootUri = serverRootUri,
          flickrOAuthSession = flickrOAuthSession)
      }
      describe("for DBpedia Search")(pending)
    }

    describe("should generate valid RDF graphs") {
      describe("for Geo Search")(pending)
      describe("for DBpedia Search")(pending)
    }
  }
}
