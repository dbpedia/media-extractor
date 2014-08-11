package org.dbpedia.media_extractor.lookup_provider.youtube

import org.dbpedia.media_extractor.lookup_provider.LookupProvider
import org.dbpedia.media_extractor.lookup_provider.MediaProviderOAuthSession
import org.scribe.builder.api.GoogleApi

abstract class YouTubeLookupProvider(
  val targetLicenses: String,
  val mediaProviderOAuthSession: MediaProviderOAuthSession[GoogleApi])

  extends LookupProvider(mediaProviderOAuthSession, targetLicenses) {
  // TODO: complete this empty stub
}
