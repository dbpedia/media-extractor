/**
 *
 */
package org.dbpedia.media_extractor.flickr

import java.net.URI
import java.util.Properties
import java.util.Scanner

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Token
import org.scribe.model.Verb
import org.scribe.model.Verifier

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

class FlickrOAuthSession(
  val savedCredentialsFile: String = "/flickr.setup.properties",
  val savedAccessTokenFile: String = "/flickr.accessToken.properties") {

  val savedAccessCredentialsProperties = loadPropertyFromFile(savedCredentialsFile)

  val myApiKey = savedAccessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = savedAccessCredentialsProperties.getProperty("apiKeySecret")

  val flickrOAuthService = new ServiceBuilder()
    .provider(classOf[FlickrApi])
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  val accessToken: Token =
    if ((!savedAccessTokenFile.isEmpty()) && (!(getSavedFlickrAccessToken(savedAccessTokenFile).isEmpty)))
      getSavedFlickrAccessToken(savedAccessTokenFile)
    else {
      val requestToken = flickrOAuthService.getRequestToken()
      val authorizationUri = flickrOAuthService.getAuthorizationUrl(requestToken)

      println("Follow this authorization URL to authorise yourself on Flickr:")
      println(authorizationUri)
      println("Paste here the verifier it gives you:")
      print(">>")

      val scanner = new Scanner(System.in)
      val verifier = new Verifier(scanner.nextLine())
      scanner.close()

      println("")
      println("(If it does not crash immediately after this line, the authorization from Flickr was successful)")

      flickrOAuthService.getAccessToken(requestToken, verifier)
    }

  def loadPropertyFromFile(propertyFile: String): Properties = {
    val propertyInputStream = this.getClass().getResourceAsStream(propertyFile)
    val myProperty = new Properties()

    myProperty.load(propertyInputStream)
    propertyInputStream.close()

    myProperty
  }

  def getSavedFlickrAccessToken(savedAccessTokenFile: String): Token = {
    val accessCredentialsProperties = loadPropertyFromFile(savedAccessTokenFile)

    val accessToken = accessCredentialsProperties.getProperty("accessToken")
    val accessSecret = accessCredentialsProperties.getProperty("accessSecret")

    new Token(accessToken, accessSecret)
  }

  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, FlickrOAuthSession.endPointUri.toString())
    request.addQuerystringParameter("method", method)

    if (signRequest)
      flickrOAuthService.signRequest(accessToken, request)

    request.send()
  }

  def getFlickrSearchResponse(searchText: String = "", latitude: String = "", longitude: String = "", radius: String = "", license: String = "", signRequest: Boolean = true): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, FlickrOAuthSession.endPointUri.toString())

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("text", searchText)
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("radius", radius)
    searchRequest.addQuerystringParameter("license", license)
    searchRequest.addQuerystringParameter("per_page", "30") // maximum according to FlickrAPI's TOU
    searchRequest.addQuerystringParameter("sort", "relevance")
    searchRequest.addQuerystringParameter("min_taken_date", "1800-01-01 00:00:00") // limiting agent to avoid "parameterless searches"

    // This request does not need to be signed
    if (signRequest)
      flickrOAuthService.signRequest(accessToken, searchRequest)

    searchRequest.send()
  }

}

object FlickrOAuthSession {

  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def apply(credentialsFile: String,
    accessTokenFile: String) =
    new FlickrOAuthSession(credentialsFile, accessTokenFile)

}