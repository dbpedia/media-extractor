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
        val flickrMediaProvider2= new FlickrMediaProvider(FlickrOAuthSession())
      }
    }

    describe("should be able to perform a simple search")

  }

}
