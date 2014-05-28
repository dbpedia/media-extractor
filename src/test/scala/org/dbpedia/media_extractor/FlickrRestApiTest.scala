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

    it("should be able to connect to Flickr") {

      println("=== Flickr's OAuth workflow ===")

      val endPointUri = new URI("https://api.flickr.com/services/rest/")

      println("About to load settings from an external file...")
      val in = classOf[FlickrRestApiTest].getResourceAsStream("/flickr.setup.properties")
      val properties = new Properties()
      properties.load(in)
      in.close()
      println("Settings loaded")

      println("Building myFlickrService...")
      val myFlickrService: OAuthService = new ServiceBuilder().provider(classOf[FlickrApi]).apiKey(properties.getProperty("apiKey")).apiSecret(properties.getProperty("secret")).build()

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
      val tokenKey: String = scanner.nextLine()
      scanner.close()

      println("About to trade the request token and the verifier for an access token...")
      val accessToken = myFlickrService.getAccessToken(requestToken, new Verifier(tokenKey))
      println("Access token: " + accessToken)

      println("Authentication success")

      println("Building the access request to a protected resource...")
      val authRequest = new OAuthRequest(Verb.POST, endPointUri.toString())
      authRequest.addQuerystringParameter("method", "flickr.test.login")
      myFlickrService.signRequest(accessToken, authRequest)
      
      println("About to access a protected resource...")
      val response = authRequest.send()
      println("Got it! Let's see what we found...")
      println("Response:")
      println("Body: " + response.getBody())
      println("Code: " + response.getCode())
      println("Message: " + response.getMessage())
      println("Headers: " + response.getHeaders())
      println("Stream: " + response.getStream())
      
    }
    it("should get at least one picture")(pending)

  }
}