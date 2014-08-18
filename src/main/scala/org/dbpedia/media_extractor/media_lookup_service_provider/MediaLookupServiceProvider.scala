package org.dbpedia.media_extractor.media_lookup_service_provider

import org.scribe.builder.api.Api
import org.dbpedia.media_extractor.lookup_service.GeoLookupService
import org.dbpedia.media_extractor.lookup_service.SemanticLookupService
import org.dbpedia.media_extractor.oauthsession.OAuthSession
import org.dbpedia.media_extractor.search_result.SearchResult
import org.dbpedia.media_extractor.lookup_service.SemanticLookupService
import org.dbpedia.media_extractor.lookup_service.GeoLookupService

class MediaLookupServiceProvider[ProviderApi <: Api, SearchResultType <: SearchResult](
  val myProviderApi: ProviderApi,
  val savedCredentialsFile: String,
  val savedAccessTokenFile: String) {

  val oAuthSession = OAuthSession(
    myProviderApi,
    savedCredentialsFile,
    savedAccessTokenFile)

  val geoLookupService = GeoLookupService
  val semanticLookupService = SemanticLookupService

  val measurementUnit = "km"

  val termsOfUseUri: String
  val endPointRootUri: String

  val maxResultsPerQuery: String
  val targetLicenses: String

}

object MediaLookupServiceProvider {

  def apply[ProviderApi, SearchResultType](
    myProviderApi: ProviderApi,
    savedCredentialsFile: String,
    savedAccessTokenFile: String) =

    new MediaLookupServiceProvider[ProviderApi, SearchResultType](
      myProviderApi,
      savedCredentialsFile,
      savedAccessTokenFile)
}
