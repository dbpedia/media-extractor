/**
 *
 */
package org.dbpedia.media_extractor

import org.scalatest.FunSpec
import com.flickr4java.flickr.test.TestInterface
import com.flickr4java.flickr.Flickr
import com.flickr4java.flickr.REST
import java.util.HashMap

/**
 * @author allentiak
 *
 */
class FlickrGatewayTest extends FunSpec{
  
  describe("A FlickrGateway") {
    it("should be able to exist :-)")
    {
      val myAPIKey="myKey"
      val mySecret="mySecret"
      var myFlickrGateway= new Flickr(myAPIKey,mySecret,new REST())
      val myTestInterface = myFlickrGateway.getTestInterface()
      println("myTestInterface="+myTestInterface.toString())
      var myHashMap= new HashMap[String,String]
      myHashMap.put("Marco","Polo")
      val results = myTestInterface.echo(myHashMap)
      println("results="+results.toString())
      assert (results===myAPIKey)
    }
  }

 // def main(args: Array[String]): Unit = {}

}