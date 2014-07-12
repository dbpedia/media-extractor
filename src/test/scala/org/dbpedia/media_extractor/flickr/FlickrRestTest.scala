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

    it("should load non-empty Flickr credentials from an external file, generate a session with those credentials and use that session to invoke Flickr methods") {
      // I don't know how to correctly test whether the instance was successfully created
      // Maybe adding a return type of 0 (zero) to the constructor?
      val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")

      it("should load an already created access token from a file") {
        val generatedToken = flickrOAuthSession.accessToken
        val savedToken = FlickrOAuthSession.getSavedFlickrAccessToken("/flickr.accessToken.properties")
        assert(generatedToken === savedToken)
      }

      it("should invoke test methods") {
        it("should invoke method 'flickr.test.echo'") {
          //FIXME: correct method invocation
          val echoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo")
          assert(echoResponse.getMessage() === "OK")
        }

        it("should invoke method 'flickr.test.login'") {
          //FIXME: correct method invocation
          val loginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login")
          assert(loginResponse.getMessage() === "OK")
        }

        it("should invoke method 'flickr.test.null'") {
          //FIXME: correct method invocation
          val nullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null")
          assert(nullResponse.getMessage() === "OK")
        }
      }

      it("should invoke method 'flickr.photos.search'") {

        it("should get at least one picture") {

          /*
        Brussels:
        latitude: 50°51′0″N
        longitude: 4°21′0″E
        */

          val searchText = ""
          val lat = 50.85
          val lon = 4.35

          val license = "1,2"

          //FIXME: correct method invocation
          val searchResponse = getFlickrSearchResponse(searchText, lat, lon, license)

        }

        it("should show the photos' links")(pending)

      }
    }

  }
}
