/**
 *
 */
package org.dbpedia.media_extractor.search_provider

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

abstract trait SearchBehavior {
  def search
}
trait GeoSearchBehavior extends SearchBehavior {
  //TODO: implement stub
  override def search = ???
}
trait SemanticSearchBehavior extends SearchBehavior {
  //TODO: implement stub
  override def search = ???
}

abstract trait AuthenticationBehavior {
  def authenticate
}
trait OAuthBehavior extends AuthenticationBehavior {
  //TODO: implement stub
  override def authenticate = ???
}

// The singleton to generate, store and load access credentials
object OAuthSession

abstract class SearchProvider {
  def performSearchBehavior
  def setSearchBehavior
  def performAuthenticationBehavior
  def setAuthenticationBehavior
}

//TODO: implement stubs
case class FlickrSearchProvider() extends SearchProvider {
  override def performSearchBehavior = ???
  override def setSearchBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}

//TODO: implement stubs
case class CreativeCommonsSearchProvider() extends SearchProvider {
  override def performSearchBehavior = ???
  override def setSearchBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}

//TODO: implement stubs
case class YouTubeSearchProvider() extends SearchProvider {
  override def performSearchBehavior = ???
  override def setSearchBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}