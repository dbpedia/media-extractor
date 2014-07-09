/**
 *
 */
package org.dbpedia.media_extractor.flickr

import java.net.URI
import java.util.Properties
import java.util.Scanner
import scala.xml.XML
import org.scalatest.FunSpec
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Verifier
import com.hp.hpl.jena.rdf.model._
import org.apache.jena.vocabulary._
import org.scribe.builder.api.FlickrApi

import org.dbpedia.media_extractor.flickr._

/**
 * @author allentiak
 *
 */
class FlickrRestApiTest extends FunSpec {

  describe("A Flickr instance") {

    it("should log in into Flickr and do stuff") {

      it("should load non-empty Flickr credentials from an external file, generate a session with those credentials, and save the token into a file") {
        //FIXME: correct method invocation
        val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")
        flickrOAuthSession.postCreate()
      }
      //TODO: correct method invocation
      it("should load the token")(pending)

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

          val searchResponse = getFlickrSearchResponse(searchText, lat, lon, license)

        }

        it("should show the photos' links")(pending)

      }
    }

  }
}
