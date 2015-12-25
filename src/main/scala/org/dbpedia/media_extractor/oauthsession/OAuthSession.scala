package org.dbpedia.media_extractor.oauthsession

import java.util.Properties
import java.util.Scanner

import com.github.scribejava.core.model.Token
import com.github.scribejava.core.model.Verifier

abstract class OAuthSession(
  val savedCredentialsFile: String,
  val savedAccessTokenFile: String) {

  val savedAccessCredentialsProperties = loadPropertyFromFile(savedCredentialsFile)

  val myApiKey = savedAccessCredentialsProperties.getProperty("apiKey")
  val myApiKeySecret = savedAccessCredentialsProperties.getProperty("apiKeySecret")

  val myOAuthServiceBuilder: OAuthServiceBuilder

  lazy val accessToken: Token = {

    val myOAuthService = myOAuthServiceBuilder.oAuthService

    val accessToken =
      if ((!savedAccessTokenFile.isEmpty()) && (!(getSavedAccessToken(savedAccessTokenFile).isEmpty)))
        getSavedAccessToken(savedAccessTokenFile)
      else {

        // Make OAuth2 use transparent to myOAuthService
        val requestToken = myOAuthServiceBuilder.requestToken
        val authorizationUri = myOAuthServiceBuilder.getAuthorizationUrl()

        println("Follow this authorization URL to authorise yourself on " + myOAuthServiceBuilder.providerName + ":")
        println(authorizationUri)
        println("Paste here the verifier it gives you:")
        print(">>")

        val scanner = new Scanner(System.in)
        val verifier = new Verifier(scanner.next())
        scanner.close()
        println("")

        val generatedAccessToken = myOAuthService.getAccessToken(requestToken, verifier)

        println("Generated " + myOAuthServiceBuilder.providerName + " Access Token: (keep it secret!!)")
        println(generatedAccessToken)
        println("")

        generatedAccessToken
      }

    accessToken
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
