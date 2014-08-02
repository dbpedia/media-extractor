/**
 *
 */
package org.dbpedia.media_extractor.search_provider

/**
 * @author Leandro Doctors (ldoctors at gmail dot com)
 *
 */

abstract trait LookupBehavior {
  def search()
}
trait GeoLookupBehavior extends LookupBehavior {
  //TODO: implement stub
  override def search = ???
}
trait SemanticLookupBehavior extends LookupBehavior {
  //TODO: implement stub
  override def search = ???
}

abstract trait AuthenticationBehavior {
  def authenticate()
}

trait OAuthBehavior extends AuthenticationBehavior {
  //TODO: implement stub
  override def authenticate = ???
}

// The singleton to generate, store and load access credentials
object OAuthSession

abstract class LookupProvider {
  def performLookupBehavior()
  def setLookupBehavior()
  def performAuthenticationBehavior()
  def setAuthenticationBehavior()
}

//TODO: implement stubs
case class FlickrSearchProvider() extends LookupProvider {
  override def performLookupBehavior = ???
  override def setLookupBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}

//TODO: implement stubs
case class CreativeCommonsLookupProvider() extends LookupProvider {
  override def performLookupBehavior = ???
  override def setLookupBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}

//TODO: implement stubs
case class YouTubeLookupProvider() extends LookupProvider {
  override def performLookupBehavior = ???
  override def setLookupBehavior = ???
  override def performAuthenticationBehavior = ???
  override def setAuthenticationBehavior = ???
}