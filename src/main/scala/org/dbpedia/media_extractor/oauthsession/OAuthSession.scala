package org.dbpedia.media_extractor.oauthsession

import java.util.Properties
import java.util.Scanner

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.Api
import org.scribe.model.Token
import org.scribe.model.Verifier

class OAuthSession[ProviderApi <: Api](
  val myProviderApi: ProviderApi,
  val savedCredentialsFile: String,
  val savedAccessTokenFile: String) {

  val savedAccessCredentialsProperties = loadPropertyFromFile(savedCredentialsFile)

  val myApiKey = savedAccessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = savedAccessCredentialsProperties.getProperty("apiKeySecret")

  val oAuthService = new ServiceBuilder()
    .provider(myProviderApi)
    .apiKey(myApiKey)
    .apiSecret(myApiKeySecret)
    .build()

  val accessToken: Token =
    if ((!savedAccessTokenFile.isEmpty()) && (!(getSavedAccessToken(savedAccessTokenFile).isEmpty)))
      getSavedAccessToken(savedAccessTokenFile)
    else {
      val requestToken = oAuthService.getRequestToken()
      val authorizationUri = oAuthService.getAuthorizationUrl(requestToken)

      println("Follow this authorization URL to authorise yourself on " + myProviderApi.getClass().toString() + ":")
      println(authorizationUri)
      println("Paste here the verifier it gives you:")
      print(">>")

      val scanner = new Scanner(System.in)
      val verifier = new Verifier(scanner.next())
      scanner.close()
      println("")

      val generatedAccessToken = oAuthService.getAccessToken(requestToken, verifier)

      println("Generated Access Token: (keep it secret!!)")
      println(generatedAccessToken)
      println("")

      generatedAccessToken
    }

  private def loadPropertyFromFile(propertyFile: String): Properties = {
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

object OAuthSession {

  def apply[ProviderApi <: Api](
    myProviderApi: ProviderApi,
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =

    new OAuthSession[ProviderApi](
      myProviderApi: ProviderApi,
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)
}
