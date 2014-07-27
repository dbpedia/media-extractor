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
 * @author allentiak
 *
 */

class FlickrOAuthSession(val credentialsFile: String) {

  val accessCredentialsInputStream = this.getClass().getResourceAsStream(credentialsFile)
  val accessCredentials = new Properties()

  accessCredentials.load(accessCredentialsInputStream)
  accessCredentialsInputStream.close()
  val myApiKey = accessCredentials.getProperty("apiKey")
  val myApiKeySecret = accessCredentials.getProperty("apiKeySecret")

  val flickrOAuthService = new ServiceBuilder()
    .provider(classOf[FlickrApi])
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

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

  val accessToken = flickrOAuthService.getAccessToken(requestToken, verifier)
  println("Authentication success")
  
  //TODO: move to a test? this is for testing purposes only...
  //e. g. method = "flickr.test.login"
  def invoke_parameterless_method(method: String = null, signRequest: Boolean = true): Response = {
    val request = new OAuthRequest(Verb.POST, FlickrOAuthSession.endPointUri.toString())
    request.addQuerystringParameter("method", method)

    if (signRequest)
      flickrOAuthService.signRequest(accessToken, request)

    request.send()
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

}