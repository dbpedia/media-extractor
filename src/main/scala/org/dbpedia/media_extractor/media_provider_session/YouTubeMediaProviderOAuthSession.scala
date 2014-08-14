package org.dbpedia.media_extractor.media_provider_session

import org.scribe.builder.api.GoogleApi

class YouTubeMediaProviderOAuthSession(
  targetLicenses: String,
  mediaProviderOAuthSession: MediaProviderOAuthSession[GoogleApi])

  extends MediaProviderOAuthSession(mediaProviderOAuthSession, targetLicenses) {
  // TODO: complete this empty stub
}
