package org.dbpedia.media_extractor.lookup_provider

abstract class MediaProvider(
  val mediaProviderOAuthSession: MediaProviderOAuthSession[_],
  val targetLicenses: String) {
  // TODO: complete this empty stub
}
