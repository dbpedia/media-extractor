/**
 *
 */
package org.dbpedia.media_extractor

import java.util.HashMap

import org.scalatest.FunSpec

import com.flickr4java.flickr.Flickr
import com.flickr4java.flickr.FlickrException
import com.flickr4java.flickr.REST
import com.flickr4java.flickr.auth.Auth
import com.flickr4java.flickr.auth.AuthInterface
import com.flickr4java.flickr.auth.Permission
import com.flickr4java.flickr.util.IOUtilities

import org.scribe.model.Token
import org.scribe.model.Verifier

import java.io.IOException
import java.io.InputStream
import java.util.Properties
import java.util.Scanner

/**
 * @author allentiak
 *
 */
class FlickrTest extends FunSpec {

  describe("A Flickr instance") {

    it("should be able to connect to Flickr") {
      var properties: Properties = null

      def auth() = {
        var in: InputStream = null
        try {
          var in = classOf[FlickrTest].getResourceAsStream("/flickr.setup.properties")
          properties = new Properties()
          properties.load(in)
        } finally {
          IOUtilities.close(in)
        }

        val myFlickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST())

        Flickr.debugStream = false
        val authInterface = myFlickr.getAuthInterface()

        val scanner = new Scanner(System.in)

        val token = authInterface.getRequestToken()
        println("token: " + token)

        val url = authInterface.getAuthorizationUrl(token, Permission.READ)
        println("Follow this URL to authorise yourself on Flickr")
        println(url)
        println("Paste in the token it gives you:")
        print(">>")

        val tokenKey: String = scanner.nextLine()
        scanner.close()

        val requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey))
        println("Authentication success")

        val auth = authInterface.checkToken(requestToken)

        // This token can be used until the user revokes it.
        println("Token: " + requestToken.getToken())
        println("nsid: " + auth.getUser().getId())
        println("Realname: " + auth.getUser().getRealName())
        println("Username: " + auth.getUser().getUsername())
        println("Permission: " + auth.getPermission().getType())

        auth
      }

      def main(args: Array[String]=null):Int ={
        try {
          auth()
        } catch {
          case e: Exception => e.printStackTrace()
        }
        0
      }
      
      assert (main()===0)
    }

    it("should get at least one picture")(pending)

  }
}