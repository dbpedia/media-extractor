package org.dbpedia.media_extractor.lookup_provider

import org.dbpedia.media_extractor.lookup_provider.flickr.FlickrLookupProvider
import org.scribe.builder.api.GoogleApi

class YouTubeSemanticLookupServiceProvider(
  override val targetLicenses: String,
  override val lookupOAuthSession: LookupOAuthSession[GoogleApi],
  val targetResource: String = "Brussels",
  override val radius: String = "5")

  extends SemanticLookupService(
    targetResource,
    radius)

  with YouTubeLookupProvider(
    targetLicenses,
    lookupOAuthSession) {

}
