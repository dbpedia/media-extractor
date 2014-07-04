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

trait OAuthSession {
  val credentialsFile: String
  def postCreate(): Unit = ???
  def preDestroy(): Unit = ???
}

class FlickrOAuthSessionImpl(val credentialsFile: String) extends OAuthSession {
  val myFlickrService = Nil

  def postCreate = {
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
  }
}

private class OAuthSessionDelegate extends OAuthSession {
  def credentialsFile = OAuthSessionManager.instance.credentialsFile
}

object OAuthSessionManager {
  val flickrConfigFile = ""

  private[this] var _instance: Option[OAuthSession] = None

  private[flickr] def instance: OAuthSession = {
    if (_instance isEmpty) {
      _instance = Option(new FlickrOAuthSessionImpl(flickrConfigFile))
      _instance.get.postCreate
    }
    _instance.get
  }

  def destroy = {
    if (_instance nonEmpty) {
      _instance.get.preDestroy
      _instance = None
    }
  }

  def session: OAuthSession = new OAuthSessionDelegate()

  def flickrAuth() = {

    val requestToken = FlickrOAuthSession.myFlickrService.getRequestToken()
    val authorizationUri = FlickrOAuthSession.myFlickrService.getAuthorizationUrl(requestToken)

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