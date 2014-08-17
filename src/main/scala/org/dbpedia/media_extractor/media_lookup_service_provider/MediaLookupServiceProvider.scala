package org.dbpedia.media_extractor.media_lookup_service_provider

import org.scribe.builder.api.Api
import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.lookup_service.GeoLookupService
import org.dbpedia.media_extractor.lookup_service.SemanticLookupService

abstract class MediaLookupServiceProvider[ProviderApi <: Api] {
  val oAuthSession: OAuthSession[ProviderApi]
  val geoLookupService = GeoLookupService
  val semanticLookupService = SemanticLookupService

  val measurementUnit = "km"

  val termsOfUseUri: String
  val endPointRootUri: String

  val maxResultsPerQuery: String
  val targetLicenses: String

}
