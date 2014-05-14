/**
 *
 */
package org.dbpedia.media_extractor

import org.scalatest.FunSpec
import com.flickr4java.flickr.test.TestInterface
import com.flickr4java.flickr.Flickr
import com.flickr4java.flickr.REST
import java.util.HashMap
import java.util.Collections
import java.util.Collection
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
import java.util.Scanner;
import org.scalatest.BeforeAndAfter

/**
 * @author allentiak
 *
 */
class FlickrTest extends FunSpec {

  describe("A Flickr instance") {

    val myAPIKey = "myAPIKey"
    val mySecret = "mySecret"
    var myFlickr = new Flickr(myAPIKey, mySecret, new REST())

    var myHashMap = new HashMap[String, String]
    myHashMap.put("api_key", myAPIKey)

    it("should throw a FlickrException if no API key is supplied when echoing") {

      val myTestInterface = myFlickr.getTestInterface()
      intercept[FlickrException] {
        var testInterfaceResults = myTestInterface.echo(new HashMap)
      }
    }

    it("should echo \"api_key\" if the API key is supplied when echoing") {

      val myTestInterface = myFlickr.getTestInterface()
      var testInterfaceResults = myTestInterface.echo(myHashMap)
      assert(testInterfaceResults.toString().contains("api_key"))
    }

    it("should echo \"method\" if the API key is supplied when echoing") {

      val myTestInterface = myFlickr.getTestInterface()
      var testInterfaceResults = myTestInterface.echo(myHashMap)
      assert(testInterfaceResults.toString().contains("method"))
    }
    
    it("should get at least one picture") (pending)

  }

}