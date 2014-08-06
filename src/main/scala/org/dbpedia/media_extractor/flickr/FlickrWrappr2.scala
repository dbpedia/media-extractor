package org.dbpedia.media_extractor.flickr

/**
 * @author "Leandro Doctors (ldoctors at gmail dot com)"
 *
 */

class FlickrWrappr2(val serverRootUri: String = "http://localhost/flickrwrappr/",
  val flickrSavedCredentialsFile: String = "/flickr.setup.properties",
  val flickrSavedAccessTokenFile: String = "/flickr.accessToken.properties") {

  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"
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
    locationRootUri = locationRootUri,
    dataRootUri = dataRootUri,
    serverRootUri = serverRootUri,
    flickrOAuthSession = flickrOAuthSession)

  val flickrDBpediaLookup = FlickrDBpediaLookup(
    targetResource = targetResource,
    serverRootUri = serverRootUri,
    flickrOAuthSession = flickrOAuthSession)
}

object FlickrWrappr2 extends App {
  def apply(
    serverRootUri: String = "http://localhost/flickrwrappr/",
    flickrSavedCredentialsFile: String = "/flickr.setup.properties",
    flickrSavedAccessTokenFile: String = "/flickr.accessToken.properties") =
    new FlickrWrappr2(serverRootUri, flickrSavedCredentialsFile, flickrSavedAccessTokenFile)
}
