package org.dbpedia.media_extractor.media_provider.flickr

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.scribe.builder.api.FlickrApi
import org.dbpedia.media_extractor.media_provider.MediaProviderOAuthSession

class FlickrSemanticMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: MediaProviderOAuthSession[FlickrApi],
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
