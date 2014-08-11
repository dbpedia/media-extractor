package org.dbpedia.media_extractor.media_provider.flickr

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.dbpedia.media_extractor.media_provider.MediaProviderOAuthSession
import org.scribe.builder.api.FlickrApi

class FlickrGeoMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: MediaProviderOAuthSession[FlickrApi],
  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
