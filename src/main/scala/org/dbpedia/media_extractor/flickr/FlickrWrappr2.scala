package org.dbpedia.media_extractor.flickr

import scala.xml.Elem
import scala.collection.mutable.ListBuffer
import java.util.Properties
import java.net.URI
import org.scribe.builder.api.FlickrApi
import org.scribe.builder.ServiceBuilder
import java.util.Scanner
import org.scribe.model.Verifier
import org.scribe.model.Token
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Response

case class SearchResult(depictionUri: String, pageUri: String)

object FlickrWrappr2 extends App {
  val endPointUri = new URI("https://api.flickr.com/services/rest/")

  def generateLinksList(myXml: Elem): List[SearchResult] = {
    val resultsListBuffer = new ListBuffer[SearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += SearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def flickrAuth(credentialsFile: String) = {

    val accessCredentials = new Properties()

    val inputFile = this.getClass().getResourceAsStream(credentialsFile)
    accessCredentials.load(inputFile)
    inputFile.close()

    val myFlickrService = new ServiceBuilder()
      .provider(classOf[FlickrApi])
      .apiKey(accessCredentials.getProperty("apiKey"))
      .apiSecret(accessCredentials.getProperty("apiKeySecret"))
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

  def getSavedFlickrAccessToken(accessTokenFile: String): Token = {
    val accessCredentials = new Properties()
    val inputFile = this.getClass().getResourceAsStream(accessTokenFile)
    accessCredentials.load(inputFile)
    inputFile.close()

    val accessToken = accessCredentials.getProperty("accessToken")
    val accessSecret = accessCredentials.getProperty("accessSecret")

    new Token(accessToken, accessSecret)
  }

  def getFlickrSearchResponse(text: String = "", latitude: String = "", longitude: String = "", license: String = ""): Response = {
    val searchRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

    searchRequest.addQuerystringParameter("method", "flickr.photos.search")
    searchRequest.addQuerystringParameter("lat", latitude)
    searchRequest.addQuerystringParameter("lon", longitude)
    searchRequest.addQuerystringParameter("license", license)
    searchRequest.addQuerystringParameter("per_page", "30") // maximum according to FlickrAPI's TOU
    searchRequest.addQuerystringParameter("sort", "relevance")

    // This request does not need to be signed
    searchRequest.send()
  }

  def validateFlickrSearchResponse(flickrSearchResponse: Response): Boolean = {
    flickrSearchResponse.getCode().equals("200")
  }
}
