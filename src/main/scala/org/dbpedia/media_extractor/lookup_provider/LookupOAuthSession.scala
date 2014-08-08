package org.dbpedia.media_extractor.lookup_provider

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
import org.scribe.builder.api.FlickrApi
import org.scribe.builder.api.Api

// FIXME: find a more elegant way to pass the class as a parameter
class LookupOAuthSession[T <: Api](
  val myApi: T,
  val savedCredentialsFile: String = "/flickr.setup.properties",
  val savedAccessTokenFile: String = "/flickr.accessToken.properties") {

  val savedAccessCredentialsProperties = loadPropertyFromFile(savedCredentialsFile)

  val myApiKey = savedAccessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = savedAccessCredentialsProperties.getProperty("apiKeySecret")

  val flickrOAuthService = new ServiceBuilder()
    .provider(myApi.getClass())
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  val accessToken: Token =
    if ((!savedAccessTokenFile.isEmpty()) && (!(getSavedAccessToken(savedAccessTokenFile).isEmpty)))
      getSavedAccessToken(savedAccessTokenFile)
    else {
      val requestToken = flickrOAuthService.getRequestToken()
      val authorizationUri = flickrOAuthService.getAuthorizationUrl(requestToken)

      println("Follow this authorization URL to authorise yourself on " + myApi.getClass().toString() + ":")
      println(authorizationUri)
      println("Paste here the verifier it gives you:")
      print(">>")

      val scanner = new Scanner(System.in)
      val verifier = new Verifier(scanner.nextLine())
      scanner.close()

      println("")
      println("(If it does not crash immediately after this line, the authorization from " + myApi.getClass().toString() + " was successful)")

      flickrOAuthService.getAccessToken(requestToken, verifier)
    }

  def loadPropertyFromFile(propertyFile: String): Properties = {
    val propertyInputStream = this.getClass().getResourceAsStream(propertyFile)
    val myProperty = new Properties()

    myProperty.load(propertyInputStream)
    propertyInputStream.close()

    myProperty
  }

  def getSavedAccessToken(savedAccessTokenFile: String): Token = {
    val accessCredentialsProperties = loadPropertyFromFile(savedAccessTokenFile)

    val accessToken = accessCredentialsProperties.getProperty("accessToken")
    val accessSecret = accessCredentialsProperties.getProperty("accessSecret")

    new Token(accessToken, accessSecret)
  }

}

object LookupOAuthSession {

  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def apply(credentialsFile: String,
    accessTokenFile: String) =
    // FIXME: correctly use the type parameter
    new LookupOAuthSession[FlickrApi](myApi, credentialsFile, accessTokenFile)

}