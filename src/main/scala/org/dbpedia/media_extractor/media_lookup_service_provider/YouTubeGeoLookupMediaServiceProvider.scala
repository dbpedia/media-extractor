package org.dbpedia.media_extractor.media_lookup_service_provider

import org.dbpedia.media_extractor.media_provider_session.YouTubeMediaProviderOAuthSession

class YouTubeGeoLookupMediaServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: YouTubeMediaProviderOAuthSession,
  val lat: String = "50.85",
  val lon: String = "4.35",
  val radius: String = "5")

  extends YouTubeMediaLookupServiceProvider {
  //TODO: complete empty stub
}
