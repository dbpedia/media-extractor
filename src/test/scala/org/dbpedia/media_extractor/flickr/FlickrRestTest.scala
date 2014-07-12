/**
 *
 */
package org.dbpedia.media_extractor.flickr

import org.scalatest.FunSpec

/**
 * @author allentiak
 *
 */
class FlickrRestApiTest extends FunSpec {

  describe("A Flickr instance") {

    describe("should load non-empty Flickr credentials from an external file, generate a session with those credentials and use that session to invoke Flickr methods") {
      // I don't know how to correctly test whether the instance was successfully created
      // Maybe adding a return type of 0 (zero) to the constructor?
      val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")

      it("should load an already created access token from a file") {
        val generatedToken = flickrOAuthSession.accessToken
        val savedToken = FlickrOAuthSession.getSavedFlickrAccessToken("/flickr.accessToken.properties")
        assert(generatedToken === savedToken)
      }

      describe("should invoke test methods (unsigned)") {
        it("should invoke method 'flickr.test.echo'") {
          val echoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo", false)
          assert(echoResponse.getMessage() === "OK")
        }
        it("should invoke method 'flickr.test.login'") {
          val loginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login", false)
          assert(loginResponse.getMessage() === "OK")
        }
        it("should invoke method 'flickr.test.null'") {
          val nullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null", false)
          assert(nullResponse.getMessage() === "OK")
        }
      }

      describe("should invoke test methods (signed)") {
        it("should invoke method 'flickr.test.echo'") {
          val echoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo", true)
          assert(echoResponse.getMessage() === "OK")
        }
        it("should invoke method 'flickr.test.login'") {
          val loginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login", true)
          assert(loginResponse.getMessage() === "OK")
        }
        it("should invoke method 'flickr.test.null'") {
          val nullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null", true)
          assert(nullResponse.getMessage() === "OK")
        }
      }

      describe("should invoke method 'flickr.photos.search'") {
        it("should get at least one picture") {
          /*
        Brussels:
        latitude: 50°51′0″N
        longitude: 4°21′0″E
        */
          val searchText = ""
          val lat = "50.85"
          val lon = "4.35"
          val license = "1,2"

          val searchResponse = FlickrOAuthSession.getFlickrSearchResponse(searchText, lat, lon, license)
          assert(searchResponse.getMessage() === "OK")
        }
        it("should show the photos' links")(pending)

      }
    }

  }
}
