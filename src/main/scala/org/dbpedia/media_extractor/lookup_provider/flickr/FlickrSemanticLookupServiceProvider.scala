package org.dbpedia.media_extractor.lookup_provider

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.scribe.builder.api.FlickrApi

class FlickrSemanticLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: MediaProviderOAuthSession[FlickrApi],
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
