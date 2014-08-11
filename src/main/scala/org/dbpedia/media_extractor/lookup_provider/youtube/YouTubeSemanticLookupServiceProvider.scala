package org.dbpedia.media_extractor.lookup_provider

import org.dbpedia.media_extractor.lookup_provider.youtube.YouTubeLookupProvider
import org.scribe.builder.api.GoogleApi

class YouTubeSemanticLookupServiceProvider(
  override val targetLicenses: String,
  override val mediaProviderOAuthSession: MediaProviderOAuthSession[GoogleApi],
  val targetResource: String = "Brussels",
  override val radius: String = "5")

  extends SemanticLookupService(
    targetResource,
    radius)

  with YouTubeLookupProvider(
    targetLicenses,
    lookupOAuthSession) {

}
