package org.dbpedia.media_extractor.media_provider.flickr

import org.dbpedia.media_extractor.media_lookup_service_provider.MediaLookupServiceProvider
import org.dbpedia.media_extractor.media_provider_session.FlickrMediaProviderOAuthSession

class FlickrSemanticMediaLookupServiceProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: FlickrMediaProviderOAuthSession,
  targetResource: String = "Brussels",
  val radius: String = "5")

  extends MediaLookupServiceProvider {
  //TODO: complete empty stub
}
