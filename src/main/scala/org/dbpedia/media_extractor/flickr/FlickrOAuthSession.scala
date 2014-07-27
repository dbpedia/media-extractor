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

class FlickrOAuthSession(
  val credentialsFile: String = "/flickr.setup.properties",
  val accessTokenFile: String = "/flickr.accessToken.properties") {

  val accessCredentialsProperties = loadPropertyFromFile(credentialsFile)

  val myApiKey = accessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = accessCredentialsProperties.getProperty("apiKeySecret")

  val flickrOAuthService = new ServiceBuilder()
    .provider(classOf[FlickrApi])
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  if (!(accessTokenFile.isEmpty)) {
    val accessToken = getSavedFlickrAccessToken(accessTokenFile)
  } else {
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
  }

  def loadPropertyFromFile(propertyFile: String): Properties = {
    val propertyInputStream = this.getClass().getResourceAsStream(propertyFile)
    val myProperty = new Properties()

    myProperty.load(propertyInputStream)
    propertyInputStream.close()

    myProperty
  }

  def getSavedFlickrAccessToken(accessTokenFile: String): Token = {
    val accessCredentials = loadPropertyFromFile(accessTokenFile)

    val accessToken = accessCredentials.getProperty("accessToken")
    val accessSecret = accessCredentials.getProperty("accessSecret")

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

}

object FlickrOAuthSession {

  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def apply(credentialsFile: String) = new FlickrOAuthSession(credentialsFile)

}