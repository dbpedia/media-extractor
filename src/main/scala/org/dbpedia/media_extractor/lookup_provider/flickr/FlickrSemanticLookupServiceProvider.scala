package org.dbpedia.media_extractor.lookup_provider

import org.dbpedia.media_extractor.lookup_provider.flickr.FlickrLookupProvider
import org.scribe.builder.api.FlickrApi

class FlickrSemanticLookupServiceProvider(
  override val targetLicenses: String,
  override val lookupOAuthSession: LookupOAuthSession[FlickrApi],
  val targetResource: String = "Brussels",
  override val radius: String = "5")

  extends SemanticLookupService(
    targetResource,
    radius)

  with FlickrLookupProvider(
    targetLicenses,
    lookupOAuthSession) {

}
