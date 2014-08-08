package org.dbpedia.media_extractor.lookup_provider

import org.dbpedia.media_extractor.lookup_provider.flickr.FlickrLookupProvider
import org.scribe.builder.api.GoogleApi

class YouTubeGeoLookupServiceProvider (
  override val targetLicenses: String,
  override val lookupOAuthSession: LookupOAuthSession[GoogleApi],
  override val lat: String = "50.85",
  override val lon: String = "4.35",
  override val radius: String = "5")

  
  extends GeoLookupService (
  // By default, search for Brussels
  lat,
  lon,
  radius)

with YouTubeLookupProvider(
  targetLicenses,
  lookupOAuthSession) {

}
