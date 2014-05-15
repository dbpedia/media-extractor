/**
 *
 */
package org.dbpedia.media_extractor

import java.util.HashMap

import org.scalatest.FunSpec

import com.flickr4java.flickr.Flickr
import com.flickr4java.flickr.FlickrException
import com.flickr4java.flickr.REST
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.IOUtilities;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author allentiak
 *
 */
object FlickrTest extends FunSpec {

  describe("A Flickr instance") {
       
    var properties: Properties = null

    def auth() {      
      var in: InputStream = null;
      try {
        var in = classOf[AuthExample].getResourceAsStream("/flickr.setup.properties");
        properties = new Properties();
        properties.load(in);
      } finally {
        IOUtilities.close(in);
      }
    }

    val myFlickr = new Flickr(properties.getProperty("apiKey"), properties.getProperty("secret"), new REST());

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

    it("should get at least one picture")(pending)

  }

}