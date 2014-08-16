package org.dbpedia.media_extractor.media_provider.youtube

import org.dbpedia.media_extractor.media_provider_session.YouTubeMediaProviderOAuthSession
import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider

class YouTubeSemanticMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: YouTubeMediaProviderOAuthSession,
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
