package org.dbpedia.media_extractor.media_lookup_service_provider

import org.dbpedia.media_extractor.media_provider_session.YouTubeMediaProviderOAuthSession

class YouTubeSemanticMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: YouTubeMediaProviderOAuthSession,
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends YouTubeMediaLookupServiceProvider {
  //TODO: complete empty stub
}
