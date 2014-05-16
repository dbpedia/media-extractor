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
import org.scribe.exceptions.OAuthException

/**
 * @author allentiak
 *
 */
class FlickrTest extends FunSpec {

  describe("A Flickr instance") {

    it("should be able to connect to Flickr") (pending)
    it("should get at least one picture")(pending)

  }
}