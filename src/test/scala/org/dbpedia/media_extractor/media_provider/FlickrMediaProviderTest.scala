package org.dbpedia.media_extractor.media_provider

import org.scalatest.FunSpec
import org.dbpedia.media_extractor.oauthsession.FlickrOAuthSession

class FlickrMediaProviderTest extends FunSpec {

  describe("a FlickrMediaProvider") {

    describe("should be able to connect to Flickr") {

      it("by creating an OAuthSession instance if needed") {
        val flickrOAuthSession = FlickrOAuthSession()

        val flickrMediaProvider = new FlickrMediaProvider(flickrOAuthSession)
      }

      it("by reusing an OAuthSession instance if possible") {
      // FIXME: fix this one
        val flickrOAuthSession1 = FlickrOAuthSession()
        val flickrOAuthSession2 = FlickrOAuthSession()

        assert(flickrOAuthSession1 === flickrOAuthSession2)
        val flickrMediaProvider2 = new FlickrMediaProvider(FlickrOAuthSession())
      }
    }

    describe("should be able to perform a simple search") {
      val flickrOAuthSession = FlickrOAuthSession()
      val flickrMediaProvider = new FlickrMediaProvider(flickrOAuthSession)

      val searchText = "Brussels"
      val latitude = "50.85"
      val longitude = "4.35"
      val radius = "5"
      val signRequest = true

      it("should be able to get the reply from Flickr") {
        val searchResponse = flickrMediaProvider.getSearchResponse(searchText, latitude, longitude, radius, signRequest)
      }

      it("should be able to convert the reply from Flickr into a Set") {
        val searchResponse = flickrMediaProvider.getSearchResponse(searchText, latitude, longitude, radius, signRequest)
        val searchResults = flickrMediaProvider.getSearchResults(searchResponse)
      }

    }

  }

}
