/**
 *
 */
package org.dbpedia.media_extractor.flickr

import org.scribe.model.Verifier
import org.scribe.builder.api.FlickrApi
import org.scribe.builder.ServiceBuilder
import java.util.Properties
import java.util.Scanner
import org.scribe.model.Token
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import java.net.URI
import org.scribe.model.Response

/**
 * @author allentiak
 *
 */

class FlickrOAuthSession(val credentialsFile: String) {

  val inputFile = this.getClass().getResourceAsStream(credentialsFile)
  val accessCredentials = new Properties()

  accessCredentials.load(inputFile)
  inputFile.close()
  val myApiKey = accessCredentials.getProperty("apiKey")
  val myApiKeySecret = accessCredentials.getProperty("apiKeySecret")

  val myFlickrService = new ServiceBuilder()
    .provider(classOf[FlickrApi])
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  val requestToken = myFlickrService.getRequestToken()
  val authorizationUri = myFlickrService.getAuthorizationUrl(requestToken)

  println("Follow this authorization URL to authorise yourself on Flickr:")
  println(authorizationUri)
  println("Paste here the verifier it gives you:")
  print(">>")

  val scanner = new Scanner(System.in)
  val verifier = new Verifier(scanner.nextLine())
  scanner.close()

  println("")

  val accessToken = myFlickrService.getAccessToken(requestToken, verifier)
  println("Authentication success")

  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, FlickrOAuthSession.endPointUri.toString())
    request.addQuerystringParameter("method", method)

    if (signRequest)
      myFlickrService.signRequest(accessToken, request)

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
      myFlickrService.signRequest(accessToken, searchRequest)
    searchRequest.send()
  }

}

object FlickrOAuthSession {

  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def apply(credentialsFile: String) = new FlickrOAuthSession(credentialsFile)

  def getSavedFlickrAccessToken(accessTokenFile: String): Token = {
    val accessCredentials = new Properties()
    val inputFile = this.getClass().getResourceAsStream(accessTokenFile)
    accessCredentials.load(inputFile)
    inputFile.close()

    val accessToken = accessCredentials.getProperty("accessToken")
    val accessSecret = accessCredentials.getProperty("accessSecret")

    new Token(accessToken, accessSecret)
  }

  def validateFlickrSearchResponse(flickrSearchResponse: Response): Boolean = {
    flickrSearchResponse.getCode().equals("200")
  }
}