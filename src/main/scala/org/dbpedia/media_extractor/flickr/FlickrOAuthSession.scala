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

}

object FlickrOAuthSession {

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