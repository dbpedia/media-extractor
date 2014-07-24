package org.dbpedia.media_extractor.flickr

import com.hp.hpl.jena.rdf.model.ModelFactory
class FlickrWrappr2(val serverRootUri: String = "http://localhost/flickrwrappr/", val flickrCredentialsFile: String = "/flickr.setup.properties") {
  val locationRootUri = serverRootUri + "location/"
  val dataRootUri = serverRootUri + "data/photosDepictingLocation/"
  val flickrOAuthSession = FlickrOAuthSession(flickrCredentialsFile)
}

object FlickrWrappr2 extends App {

  val flickrTermsUri = "http://www.flickr.com/terms.gne"
  val flickrwrappr = "flickr(tm) wrappr"

  val myPath = "/media/allentiak/dbpedia.git/media-extractor/src/test/resources/"

  val outputMode = "RDF/XML"

  var flickrGeoSearch: FlickrGeoSearch = null
  var flickrDBpediaSearch: FlickrDBpediaSearch = null

  def apply(serverRootUri: String = "http://localhost/flickrwrappr/", flickrCredentialsFile: String = "/flickr.setup.properties") =
    new FlickrWrappr2(serverRootUri, flickrCredentialsFile)

  // FIXME: how to access flickrOAuthSession?
  // FIXME: how to access the members of the companion class?
  def performFlickrGeoSearch(lat: String, lon: String, radius: String) = {
    flickrGeoSearch = new FlickrGeoSearch(lat, lon, radius, FlickrWrappr2.locationRootUri, FlickrWrappr2.dataRootUri, FlickrWrappr2.serverRootUri)
    flickrGeoSearch.addNameSpacesToRDFGraph()
    flickrGeoSearch.addMetadataToRDFGraph()
    flickrGeoSearch.addFlickrSearchResultsToRDFGraph(generateUrisForFlickrSearchResponse(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude, longitude, radius, license, signRequest)))
  }

  // FIXME: how to access flickrOAuthSession?
  // FIXME: how to access the members of the companion class?
  def performFlickrDBpediaSearch(targetResource: String, radius: String) {
    flickrDBpediaSearch = new FlickrDBpediaSearch(targetResource, radius, FlickrWrappr2.serverRootUri)
    flickrDBpediaSearch.addNameSpacesToRDFGraph()
    flickrDBpediaSearch.addMetadataToRDFGraph()
    flickrDBpediaSearch.addFlickrSearchResultsToRDFGraph(generateUrisForFlickrSearchResponse(flickrOAuthSession.getFlickrSearchResponse(searchText = "", latitude, longitude, radius, license, signRequest)))

  }

}

