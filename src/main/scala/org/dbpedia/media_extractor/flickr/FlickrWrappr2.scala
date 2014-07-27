package org.dbpedia.media_extractor.flickr

class FlickrWrappr2(val serverRootUri: String = "http://localhost/flickrwrappr/", val flickrCredentialsFile: String = "/flickr.setup.properties") {
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"
  val flickrOAuthSession = FlickrOAuthSession(flickrCredentialsFile)
}

object FlickrWrappr2 extends App {
  val flickrOAuthSession = FlickrWrappr2.flickrOAuthSession(flickrCredentialsFile)
  var flickrGeoSearch: FlickrGeoSearch = null
  var flickrDBpediaSearch: FlickrDBpediaSearch = null

  def apply(serverRootUri: String = "http://localhost/flickrwrappr/", flickrCredentialsFile: String = "/flickr.setup.properties") =
    new FlickrWrappr2(serverRootUri, flickrCredentialsFile)

}

