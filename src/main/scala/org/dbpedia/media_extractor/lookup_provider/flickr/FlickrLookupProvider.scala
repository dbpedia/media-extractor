package org.dbpedia.media_extractor.lookup_provider.flickr

import org.dbpedia.media_extractor.lookup_provider.LookupOAuthSession
import org.dbpedia.media_extractor.lookup_provider.LookupProvider
import org.scribe.builder.api.FlickrApi

abstract class FlickrLookupProvider(
  val targetLicenses: String,
  val lookupOAuthSession: LookupOAuthSession[FlickrApi])

  extends LookupProvider(lookupOAuthSession, targetLicenses) {
  // TODO: complete this empty stub
}
