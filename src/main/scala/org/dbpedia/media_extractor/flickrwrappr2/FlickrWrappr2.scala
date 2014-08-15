package org.dbpedia.media_extractor.flickrwrappr2

class FlickrWrappr2(
  val flickrSavedCredentialsFile: String = "/flickr.setup.properties",
  val flickrSavedAccessTokenFile: String = "/flickr.accessToken.properties") {

  val flickrOAuthSession = FlickrOAuthSession(flickrSavedCredentialsFile, flickrSavedAccessTokenFile)

  // TODO: allow user to input search parameters.
  // (For now, search for Brussels by default)

  // Geo default parameters:
  val lat = "50.85"
  val lon = "4.35"
  val radius = "5"

  // DBpedia default parameter:
  val targetResource = "Brussels"

  val flickrGeoLookup = FlickrGeoLookup(
    lat = lat,
    lon = lon,
    radius = radius,
    flickrOAuthSession = flickrOAuthSession)

  val flickrDBpediaLookup = FlickrDBpediaLookup(
    targetResource = targetResource,
    flickrOAuthSession = flickrOAuthSession)
}

object FlickrWrappr2 extends App {
  def apply(
    flickrSavedCredentialsFile: String = "/flickr.setup.properties",
    flickrSavedAccessTokenFile: String = "/flickr.accessToken.properties") =
    new FlickrWrappr2(flickrSavedCredentialsFile, flickrSavedAccessTokenFile)
}
