package org.dbpedia.media_extractor.media_provider_session

import org.scalatest.FunSpec

class MediaProviderOAuthSessionTest extends FunSpec {

  describe("A MediaProviderOAuthSession instance") {

    describe("Flickr") {

      ignore("should be able to connect to Flickr using saved credentials (only <ApiKey,ApiKeySecret>)") {
        val manuallyGeneratedFlickrOAuthSession = FlickrMediaProviderOAuthSession(
          savedCredentialsFile = "/flickr.setup.properties",
          savedAccessTokenFile = "")
        assert(manuallyGeneratedFlickrOAuthSession.isInstanceOf[FlickrMediaProviderOAuthSession])
      }

      it("should be able to connect to Flickr using saved credentials (both <ApiKey,ApiKeySecret> and <accessToken,accessTokenSecret>)") {
        val automaticallyGeneratedFlickrOAuthSession = FlickrMediaProviderOAuthSession(
          savedCredentialsFile = "/flickr.setup.properties",
          savedAccessTokenFile = "/flickr.accessToken.properties")
        assert(automaticallyGeneratedFlickrOAuthSession.isInstanceOf[FlickrMediaProviderOAuthSession])

        val generatedToken = automaticallyGeneratedFlickrOAuthSession.accessToken
        val savedToken = automaticallyGeneratedFlickrOAuthSession.getSavedAccessToken("/flickr.accessToken.properties")
        assert(generatedToken === savedToken)
      }

      describe("should be able to invoke Flickr test methods") {

        val flickrMediaProviderOAuthSession = FlickrMediaProviderOAuthSession(
          savedCredentialsFile = "/flickr.setup.properties",
          savedAccessTokenFile = "/flickr.accessToken.properties")

        describe("(unsigned)") {
          it("method 'flickr.test.echo'") {
            val unsignedEchoResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.echo", false)
            assert(unsignedEchoResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.login'") {
            val unsignedLoginResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.login", false)
            assert(unsignedLoginResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.null'") {
            val unsignedNullResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.null", false)
            assert(unsignedNullResponse.getMessage() === "OK")
          }
        }

        describe("(signed)") {
          it("method 'flickr.test.echo'") {
            val signedEchoResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.echo", true)
            assert(signedEchoResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.login'") {
            val signedLoginResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.login", true)
            assert(signedLoginResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.null'") {
            val signedNullResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.null", true)
            assert(signedNullResponse.getMessage() === "OK")
          }
        }

      }

    }

    describe("YouTube") {

      it("should be able to connect to YouTube using saved credentials (only <ApiKey,ApiKeySecret>)") {
        val manuallyGeneratedYouTubeOAuthSession = YouTubeMediaProviderOAuthSession(
          savedCredentialsFile = "/youtube.setup.properties",
          savedAccessTokenFile = "")
        assert(manuallyGeneratedYouTubeOAuthSession.isInstanceOf[YouTubeMediaProviderOAuthSession])
      }

      ignore("should be able to connect to YouTube using saved credentials (both <ApiKey,ApiKeySecret> and <accessToken,accessTokenSecret>)") {
        val automaticallyGeneratedYouTubeOAuthSession = YouTubeMediaProviderOAuthSession(
          savedCredentialsFile = "/youtube.setup.properties",
          savedAccessTokenFile = "/youtube.accessToken.properties")
        assert(automaticallyGeneratedYouTubeOAuthSession.isInstanceOf[YouTubeMediaProviderOAuthSession])

        val generatedToken = automaticallyGeneratedYouTubeOAuthSession.accessToken
        val savedToken = automaticallyGeneratedYouTubeOAuthSession.getSavedAccessToken("/youtube.accessToken.properties")
        assert(generatedToken === savedToken)
      }

      ignore("should be able to invoke YouTube test methods (TBD)") {

        val flickrMediaProviderOAuthSession = FlickrMediaProviderOAuthSession(
          savedCredentialsFile = "/flickr.setup.properties",
          savedAccessTokenFile = "/flickr.accessToken.properties")

        describe("(unsigned)") {
          it("method 'flickr.test.echo'") {
            val unsignedEchoResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.echo", false)
            assert(unsignedEchoResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.login'") {
            val unsignedLoginResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.login", false)
            assert(unsignedLoginResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.null'") {
            val unsignedNullResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.null", false)
            assert(unsignedNullResponse.getMessage() === "OK")
          }
        }

        describe("(signed)") {
          it("method 'flickr.test.echo'") {
            val signedEchoResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.echo", true)
            assert(signedEchoResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.login'") {
            val signedLoginResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.login", true)
            assert(signedLoginResponse.getMessage() === "OK")
          }
          it("method 'flickr.test.null'") {
            val signedNullResponse = flickrMediaProviderOAuthSession.invoke_parameterless_method("flickr.test.null", true)
            assert(signedNullResponse.getMessage() === "OK")
          }
        }

      }

    }

  }
  
}
