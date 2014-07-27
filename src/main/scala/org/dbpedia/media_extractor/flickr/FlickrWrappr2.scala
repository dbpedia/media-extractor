package org.dbpedia.media_extractor.flickr

class FlickrWrappr2(val serverRootUri: String = "http://localhost/flickrwrappr/", val flickrCredentialsFile: String = "/flickr.setup.properties") {
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"
  val flickrOAuthSession = FlickrOAuthSession(flickrCredentialsFile)

  // TODO: allow user to input search parameters.
  // (For now, search for Brussels by default)

  // Geo default parameters:
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  // DBpedia default parameter:
  val targetResource = "Brussels"

  val flickrGeoLookup: FlickrLookup = FlickrGeoLookup(
    lat = lat,
    lon = lon,
    radius = radius,
    locationRootUri = locationRootUri,
    dataRootUri = dataRootUri,
    serverRootUri = serverRootUri,
    flickrOAuthSession = flickrOAuthSession)

  val flickrDBpediaLookup: FlickrLookup = FlickrDBpediaLookup(
    targetResource = targetResource,
    serverRootUri = serverRootUri,
    flickrOAuthSession = flickrOAuthSession)
}

object FlickrWrappr2 extends App {
  def apply(serverRootUri: String = "http://localhost/flickrwrappr/", flickrCredentialsFile: String = "/flickr.setup.properties") =
    new FlickrWrappr2(serverRootUri, flickrCredentialsFile)

}

