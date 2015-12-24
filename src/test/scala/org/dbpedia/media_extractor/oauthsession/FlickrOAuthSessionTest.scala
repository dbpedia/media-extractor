package org.dbpedia.media_extractor.oauthsession

import org.scalatest.FunSpec

class FlickrOAuthSessionTest extends FunSpec {

  describe("A FlickrOAuthSession instance") {

    it("should be able to connect to Flickr using saved credentials (only <ApiKey,ApiKeySecret>)") {
      val manuallyGeneratedFlickrOAuthSession = FlickrOAuthSession(
        savedCredentialsFile = "/flickr.setup.properties",
        savedAccessTokenFile = "")
      assert(!manuallyGeneratedFlickrOAuthSession.accessToken.isEmpty())
    }

    it("should be able to connect to Flickr using saved credentials (both <ApiKey,ApiKeySecret> and <accessToken,accessTokenSecret>)") {
      val automaticallyGeneratedFlickrOAuthSession = FlickrOAuthSession(
        savedCredentialsFile = "/flickr.setup.properties",
        savedAccessTokenFile = "/flickr.accessToken.properties")

      val generatedToken = automaticallyGeneratedFlickrOAuthSession.accessToken
      val savedToken = automaticallyGeneratedFlickrOAuthSession.getSavedAccessToken("/flickr.accessToken.properties")
      assert(generatedToken === savedToken)
    }

  }

}
