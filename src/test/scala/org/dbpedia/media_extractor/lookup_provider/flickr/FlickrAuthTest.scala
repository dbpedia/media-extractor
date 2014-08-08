package org.dbpedia.media_extractor.lookup_provider.flickr

import java.net.URI
import java.util.Properties
import java.util.Scanner

import org.scalatest.FunSpec
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Verifier

class FlickrAuthTest extends FunSpec {

  describe("A Flickr instance") {
    val endPointUri = new URI("https://api.flickr.com/services/rest/")

    val accessCredentials = new Properties()

    it("should log in into Flickr and do stuff") {

      val inputFile = classOf[FlickrAuthTest].getResourceAsStream("/flickr.setup.properties")
      accessCredentials.load(inputFile)
      inputFile.close()

      val apiKey = accessCredentials.getProperty("apiKey")
      val apiKeySecret = accessCredentials.getProperty("apiKeySecret")

      println("apiKey: " + apiKey.toString())
      println("apiKeySecret: " + apiKeySecret.toString())

      
      val myFlickrService = new ServiceBuilder()
        .provider(classOf[FlickrApi])
        .apiKey(apiKey)
        .apiSecret(apiKeySecret)
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
    }
  }
}
