/**
 *
 */
package org.dbpedia.media_extractor.search_provider

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

abstract trait SearchBehavior
trait GeoSearchBehavior extends SearchBehavior
trait SemanticSearchBehavior extends SearchBehavior

abstract trait AuthenticationBehavior
trait OAuthBehavior extends AuthenticationBehavior
object OAuthSession

abstract class SearchProvider {
  def performSearchBehavior
  def setSearchBehavior
  def performAuthenticationBehavior
  def setAuthenticationBehavior
}

case class FlickrSearchProvider() extends SearchProvider

case class CreativeCommonsSearchProvider() extends SearchProvider

case class YouTubeSearchProvider() extends SearchProvider