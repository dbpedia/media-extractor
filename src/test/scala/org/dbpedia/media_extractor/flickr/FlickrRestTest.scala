/**
 *
 */
package org.dbpedia.media_extractor.flickr

import org.scalatest.FunSpec
import scala.xml.XML

/**
 * @author allentiak
 *
 */
class FlickrRestApiTest extends FunSpec {

  describe("A FlickrOAuthSession instance") {

    describe("should load non-empty Flickr credentials from an external file, generate a session with those credentials and use that session to invoke Flickr methods") {
      // I don't know how to correctly test whether the instance was successfully created
      // Maybe adding a return type of 0 (zero) to the constructor?
      val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")

      it("should load an already created access token from a file") {
        val generatedToken = flickrOAuthSession.accessToken
        val savedToken = FlickrOAuthSession.getSavedFlickrAccessToken("/flickr.accessToken.properties")
        assert(generatedToken === savedToken)
      }

      describe("should properly invoke test methods (unsigned)") {
        it("method 'flickr.test.echo'") {
          val unsignedEchoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo", false)
          assert(unsignedEchoResponse.getMessage() === "OK")
        }
        it("method 'flickr.test.login'") {
          val unsignedLoginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login", false)
          assert(unsignedLoginResponse.getMessage() === "OK")
        }
        it("method 'flickr.test.null'") {
          val unsignedNullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null", false)
          assert(unsignedNullResponse.getMessage() === "OK")
        }
      }

      describe("should properly invoke test methods (signed)") {
        it("method 'flickr.test.echo'") {
          val signedEchoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo", true)
          assert(signedEchoResponse.getMessage() === "OK")
        }
        it("method 'flickr.test.login'") {
          val signedLoginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login", true)
          assert(signedLoginResponse.getMessage() === "OK")
        }
        it("method 'flickr.test.null'") {
          val signedNullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null", true)
          assert(signedNullResponse.getMessage() === "OK")
        }
      }

      describe("should properly perform and process a simple Flickr search") {
        /*
        Brussels:
        latitude: 50°51′0″N
        longitude: 4°21′0″E
        */
        val searchText = ""
        val lat = "50.85"
        val lon = "4.35"
        val radius = "5"
        val license = "1,2"

        it("method 'flickr.photos.search' (unsigned)") {
          val unsignedSearchResponse = flickrOAuthSession.getFlickrSearchResponse(searchText, lat, lon, radius, license, false)
          assert(unsignedSearchResponse.getMessage() === "OK")
        }

        it("method 'flickr.photos.search' (signed)") {
          val signedSearchResponse = flickrOAuthSession.getFlickrSearchResponse(searchText, lat, lon, radius, license, true)
          assert(signedSearchResponse.getMessage() === "OK")
        }

        describe("should generate and show the photos' links") {
          val signedSearchResponse = flickrOAuthSession.getFlickrSearchResponse(searchText, lat, lon, radius, license, true)
          assert(signedSearchResponse.getMessage() === "OK")

          val flickrXmlResponse = XML.loadString(signedSearchResponse.getBody())
          val resultElemList = FlickrWrappr2.generateUrisForFlickrSearchResponse(flickrXmlResponse)

          println("Generated URIs for a simple Flickr search:")
          for (resultElem <- resultElemList) {
            println("Picture" + " " + resultElemList.indexOf(resultElem) + 1 + "/" + resultElemList.size + ":")
            println("Page Uri: " + resultElem.pageUri)
            println("Depiction Uri: " + resultElem.depictionUri)
            // FIXME: I don't know how to "assert" this
          }
        }
      }

      describe("should generate RDF graphs") {
        describe("for Geo Search")(pending)
        describe("for DBpedia Search")(pending)
      }

      describe("should generate valid RDF graphs") {
        describe("for Geo Search")(pending)
        describe("for DBpedia Search")(pending)
      }
    }

  }
}
