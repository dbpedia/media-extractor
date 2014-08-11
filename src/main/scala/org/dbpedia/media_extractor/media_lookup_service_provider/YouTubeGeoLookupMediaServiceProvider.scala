package org.dbpedia.media_extractor.media_provider.youtube

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.scribe.builder.api.GoogleApi
import org.dbpedia.media_extractor.media_provider.MediaProviderOAuthSession

class YouTubeGeoLookupMediaServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: MediaProviderOAuthSession[GoogleApi],
  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
