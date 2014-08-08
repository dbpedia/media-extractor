package org.dbpedia.media_extractor.lookup_provider

abstract class LookupProvider(
  val lookupOAuthSession: LookupOAuthSession[_],
  val targetLicenses: String) {
  // TODO: complete this empty stub
}
