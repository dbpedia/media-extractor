/**
 *
 */
package org.dbpedia.media_extractor

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

/**
 * @author allentiak
 *
 */
class FlickrRestApiTest extends FunSpec {

  describe("A Flickr instance") {
    val endPointUri = new URI("https://api.flickr.com/services/rest/")

    val accessCredentials = new Properties()

    //FIXME: factor out this huge test into the ones specified at the end

    //Here the first sub test should start
    it("should log in into Flickr and do stuff") {

      val inputFile = classOf[FlickrRestApiTest].getResourceAsStream("/flickr.setup.properties")
      accessCredentials.load(inputFile)
      inputFile.close()
      assert(
        (accessCredentials.getProperty("apiKey") != null)
          &&
          (accessCredentials.getProperty("secret") != null))

      //Here the first sub test should end

      //Here the second sub test should start

      val myFlickrService = new ServiceBuilder()
        .provider(classOf[FlickrApi])
        .apiKey(accessCredentials.getProperty("apiKey"))
        .apiSecret(accessCredentials.getProperty("secret"))
        .build()

      println("Fetching the Request Token...")
      val requestToken = myFlickrService.getRequestToken()
      println("Request token: " + requestToken)

      println("Getting the authorization URI...")
      val authorizationUri = myFlickrService.getAuthorizationUrl(requestToken)

      println("Follow this authorization URL to authorise yourself on Flickr:")
      println(authorizationUri)
      println("Paste here the verifier it gives you:")
      print(">>")

      val scanner = new Scanner(System.in)
      val verifier = new Verifier(scanner.nextLine())
      scanner.close()

      println("About to trade the request token and the verifier for an access token...")
      val accessToken = myFlickrService.getAccessToken(requestToken, verifier)
      println("Access token: " + accessToken)

      println("Authentication success")

      //Here the second sub test should end

      //Here the flickr.test.login test should start

      println("Building the access request to the protected resource flickr.test.login...")
      val loginRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.login...")
      loginRequest.addQuerystringParameter("method", "flickr.test.login")
      myFlickrService.signRequest(accessToken, loginRequest)

      println("About to invoke method flickr.test.login...")
      val loginResponse = loginRequest.send()
      println("Response:")
      println("Body: " + loginResponse.getBody())
      println("Code: " + loginResponse.getCode())
      println("Message: " + loginResponse.getMessage())
      println("Headers: " + loginResponse.getHeaders())
      println("Stream: " + loginResponse.getStream())
      println()

      assert(loginResponse.getMessage() === "OK")

      //Here the flickr.test.login test should end

      //Here the flickr.test.echo test should start

      println("Building the access request to the protected resource flickr.test.echo...")
      val echoRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.echo...")
      echoRequest.addQuerystringParameter("method", "flickr.test.echo")

      println("Request about to be sent: ")
      println("QueryStrinParams: " + echoRequest.getQueryStringParams().toString())
      println("BodyParams: " + echoRequest.getBodyParams().toString())
      println("BodyContents: " + echoRequest.getBodyContents())
      println("Headers: " + echoRequest.getHeaders().toString())

      myFlickrService.signRequest(accessToken, echoRequest)

      println("About to invoke method flickr.test.echo...")
      val echoResponse = echoRequest.send()
      println("Response:")
      println("Body: " + echoResponse.getBody())
      println("Code: " + echoResponse.getCode())
      println("Message: " + echoResponse.getMessage())
      println("Headers: " + echoResponse.getHeaders())
      println("Stream: " + echoResponse.getStream())
      println()

      assert(echoResponse.getMessage() === "OK")
      //Here the flickr.test.echo test should end

      //Here the flickr.test.null test should start

      println("Building the access request to the protected resource flickr.test.null...")
      val nullRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.null...")
      nullRequest.addQuerystringParameter("method", "flickr.test.null")

      println("Request about to be sent: ")
      println("QueryStrinParams: " + nullRequest.getQueryStringParams().toString())
      println("BodyParams: " + nullRequest.getBodyParams().toString())
      println("BodyContents: " + nullRequest.getBodyContents())
      println("Headers: " + nullRequest.getHeaders().toString())

      myFlickrService.signRequest(accessToken, nullRequest)

      println("About to invoke method flickr.test.null...")
      val nullResponse = nullRequest.send()
      println("Response:")
      println("Body: " + nullResponse.getBody())
      println("Code: " + nullResponse.getCode())
      println("Message: " + nullResponse.getMessage())
      println("Headers: " + nullResponse.getHeaders())
      println("Stream: " + nullResponse.getStream())
      println()
      assert(nullResponse.getMessage() === "OK")

      //Here the flickr.test.null test should end

      //Here the flickr.photos.search test should start

      println("Building the access request to the protected resource flickr.photos.search...")
      var photosSearchRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

      /*
       * Brussels:
      latitude: 50°51′0″N
      longitude: 4°21′0″E
      */

      val lat = 50.85
      val lon = 4.35

      /*
       <licenses>
  			<license id="0" name="All Rights Reserved" url="" />
  			<license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
  			<license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
  			<license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
  			<license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
  			<license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
  			<license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
  			<license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
  			<license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
		</licenses>
      */

      val license = "1,2"

      println("About to construct auth request for invoking method flickr.photos.search...")
      photosSearchRequest.addQuerystringParameter("method", "flickr.photos.search")
      photosSearchRequest.addQuerystringParameter("lat", lat.toString)
      photosSearchRequest.addQuerystringParameter("lon", lon.toString)
      photosSearchRequest.addQuerystringParameter("license", license)
      photosSearchRequest.addQuerystringParameter("per_page", "10")
      photosSearchRequest.addQuerystringParameter("sort", "relevance")

      println("Request about to be sent: ")
      println("QueryStringParams: " + photosSearchRequest.getQueryStringParams().toString())
      println("BodyParams: " + photosSearchRequest.getBodyParams().toString())
      println("BodyContents: " + photosSearchRequest.getBodyContents())
      println("Headers: " + photosSearchRequest.getHeaders().toString())

      myFlickrService.signRequest(accessToken, photosSearchRequest)

      println("About to invoke method flickr.photos.search...")
      val photosSearchResponse = photosSearchRequest.send()
      println("Response:")
      println("Body (this is the XML): " + photosSearchResponse.getBody())
      println("Code (200): " + photosSearchResponse.getCode())
      println("Message (OK): " + photosSearchResponse.getMessage())
      println("Headers: " + photosSearchResponse.getHeaders())
      println("Stream: " + photosSearchResponse.getStream())
      println()

      /*

This is the answer from https://secure.flickr.com/services/api/explore/flickr.photos.search
<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
  <photos page="1" pages="4023" perpage="10" total="40229">
    <photo id="14153007660" owner="85268272@N05" secret="175c4b4cef" server="2905" farm="3" title="Flower after the rainfall (3/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14336289671" owner="85268272@N05" secret="d7247be34c" server="2895" farm="3" title="Flower after the rainfall (2/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14336289131" owner="85268272@N05" secret="c2e9f60870" server="3871" farm="4" title="Flower after the rainfall (1/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313295856" owner="85268272@N05" secret="3e62843b56" server="2898" farm="3" title="Swamps in West Brussels" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14335679414" owner="85268272@N05" secret="f346a186ed" server="2904" farm="3" title="Brussels' river" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313296716" owner="85268272@N05" secret="b3a94caa90" server="5509" farm="6" title="Stream at the park" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14121413390" owner="85268272@N05" secret="12257a844a" server="5120" farm="6" title="Sunset in Brussels at the Basilique du Sacré-Cœur" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313795253" owner="86559646@N00" secret="a09eda03ae" server="5592" farm="6" title="Jambinai @ AB Club" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14290930582" owner="86559646@N00" secret="56730d389f" server="3745" farm="4" title="Jambinai @ AB Club" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14288156014" owner="85268272@N05" secret="515595e554" server="5274" farm="6" title="Poppy against the wall" ispublic="1" isfriend="0" isfamily="0" />
  </photos>
</rsp>
*/

      val myXML = XML.loadString(photosSearchResponse.getBody)

      println("Printing all of 'photo' items (photo URI and page URI): ")
      (myXML \\ "rsp" \ "photos" \ "photo") foreach {
        photo =>
          println("https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg")
          println("https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id"))
          println()
      }

      //Here the flickr.photos.search test should end

    }

    it("should load non-empty credentials from an external file")(pending)
    it("should be able to get an access token from Flickr with those credentials")(pending)
    it("should be able to invoke flickr.test.login")(pending)
    it("should be able to invoke flickr.test.echo")(pending)
    it("should be able to invoke flickr.test.null")(pending)
    it("should get at least one picture")(pending)

  }
}
