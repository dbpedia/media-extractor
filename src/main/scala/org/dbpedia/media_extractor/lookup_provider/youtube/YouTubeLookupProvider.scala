package org.dbpedia.media_extractor.lookup_provider.youtube

import org.dbpedia.media_extractor.lookup_provider.LookupOAuthSession
import org.dbpedia.media_extractor.lookup_provider.LookupProvider
import org.scribe.builder.api.GoogleApi

abstract class YouTubeLookupProvider(
  val targetLicenses: String,
  val lookupOAuthSession: LookupOAuthSession[GoogleApi])

  extends LookupProvider(lookupOAuthSession, targetLicenses) {
  // TODO: complete this empty stub
}
