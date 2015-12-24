package org.dbpedia.media_extractor.media_provider

import org.scalatest.FunSpec
import org.dbpedia.media_extractor.oauthsession.YouTubeOAuthSession

class YouTubeMediaProviderTest extends FunSpec {

  describe("a YouTubeMediaProvider") {

    describe("should be able to connect to YouTube") {

      it("by creating an OAuthSession instance if needed") {
        val youTubeOAuthSession = YouTubeOAuthSession()

        val flickrMediaProvider = new YouTubeMediaProvider(youTubeOAuthSession)
      }

      it("by reusing an OAuthSession instance if possible") {
      // FIXME: fix this one
        val youTubeOAuthSession1 = YouTubeOAuthSession()
        val youTubeOAuthSession2 = YouTubeOAuthSession()

        assert(youTubeOAuthSession1 === youTubeOAuthSession2)
        val youTubeMediaProvider2 = new YouTubeMediaProvider(YouTubeOAuthSession())
      }
    }

    describe("should be able to perform a simple search") {
      val youTubeOAuthSession = YouTubeOAuthSession()
      val youTubeMediaProvider = new YouTubeMediaProvider(youTubeOAuthSession)

      val searchText = "Brussels"
      val latitude = "50.85"
      val longitude = "4.35"
      val radius = "5"
      val signRequest = true

      it("should be able to get the reply from YouTube") {
        val searchResponse = youTubeMediaProvider.getSearchResponse(searchText, latitude, longitude, radius, signRequest)
      }

      it("should be able to convert the reply from YouTube into a Set") {
        val searchResponse = youTubeMediaProvider.getSearchResponse(searchText, latitude, longitude, radius, signRequest)
        val searchResults = youTubeMediaProvider.getSearchResults(searchResponse)
      }

    }

  }

}
