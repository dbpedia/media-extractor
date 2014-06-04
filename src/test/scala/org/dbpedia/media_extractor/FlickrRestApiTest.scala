/**
 *
 */
package org.dbpedia.media_extractor

import java.net.URI
import java.util.Properties
import java.util.Scanner

import org.scalatest.FunSpec
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService

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
      val authRequest1 = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.login...")
      authRequest1.addQuerystringParameter("method", "flickr.test.login")
      myFlickrService.signRequest(accessToken, authRequest1)

      println("About to invoke method flickr.test.login...")
      val response1 = authRequest1.send()
      println("Response:")
      println("Body: " + response1.getBody())
      println("Code: " + response1.getCode())
      println("Message: " + response1.getMessage())
      println("Headers: " + response1.getHeaders())
      println("Stream: " + response1.getStream())
      println()

      assert(response1.getMessage() === "OK")

      //Here the flickr.test.login test should end

      //Here the flickr.test.echo test should start

      println("Building the access request to the protected resource flickr.test.echo...")
      val authRequest2 = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.echo...")
      authRequest2.addQuerystringParameter("method", "flickr.test.echo")
      myFlickrService.signRequest(accessToken, authRequest2)

      println("About to invoke method flickr.test.echo...")
      val response2 = authRequest2.send()
      println("Response:")
      println("Body: " + response2.getBody())
      println("Code: " + response2.getCode())
      println("Message: " + response2.getMessage())
      println("Headers: " + response2.getHeaders())
      println("Stream: " + response2.getStream())
      println()

      assert(response2.getMessage() === "OK")
      //Here the flickr.test.echo test should end

      //Here the flickr.test.null test should start

      println("Building the access request to the protected resource flickr.test.null...")
      val authRequest3 = new OAuthRequest(Verb.POST, endPointUri.toString())

      println("About to construct auth request for invoking method flickr.test.null...")
      authRequest3.addQuerystringParameter("method", "flickr.test.null")
      myFlickrService.signRequest(accessToken, authRequest3)

      println("About to invoke method flickr.test.null...")
      val response3 = authRequest3.send()
      println("Response:")
      println("Body: " + response3.getBody())
      println("Code: " + response3.getCode())
      println("Message: " + response3.getMessage())
      println("Headers: " + response3.getHeaders())
      println("Stream: " + response3.getStream())
      println()
      assert(response3.getMessage() === "OK")

      //Here the flickr.test.login test should end

    }

    it("should load non-empty credentials from an external file")(pending)
    it("should be able to get an access token from Flickr with those credentials")(pending)
    it("should be able to invoke flickr.test.login")(pending)
    it("should be able to invoke flickr.test.echo")(pending)
    it("should be able to invoke flickr.test.null")(pending)
    it("should get at least one picture")(pending)

  }
}