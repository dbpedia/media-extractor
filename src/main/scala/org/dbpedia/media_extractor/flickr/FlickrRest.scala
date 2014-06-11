/**
 *
 */
package org.dbpedia.media_extractor.flickr

import java.net.URI
import java.util.Properties
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import java.util.Scanner
import org.scribe.model.Verifier
import java.io.InputStream
import java.io.IOException
import org.scribe.model.Token

/**
 * @author allentiak
 *
 */
class FlickrRest extends App{

  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def authorizeApp:Token = {
    val inputFile: InputStream =
      try {
        classOf[FlickrRest].getResourceAsStream("/flickr.setup.properties")
      }

    var accessCredentials = new Properties
    accessCredentials.load(inputFile)
    inputFile.close()

    val myFlickrService = new ServiceBuilder()
      .provider(classOf[FlickrApi])
      .apiKey(accessCredentials.getProperty("apiKey"))
      .apiSecret(accessCredentials.getProperty("secret"))
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
    //This access token can be used until the user revokes it
    val accessToken = myFlickrService.getAccessToken(requestToken, verifier)
    println("Access token: " + accessToken)

    println("Authentication success")
    accessToken
  }
  
}