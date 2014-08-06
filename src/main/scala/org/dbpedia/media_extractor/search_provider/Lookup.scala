/**
 *
 */
package org.dbpedia.media_extractor.search_provider

import org.scribe.oauth.OAuthService

/**
 * @author "Leandro Doctors (ldoctors at gmail dot com)"
 *
 */

abstract class Lookup(
  val oAuthSession: OAuthService,
  val radius: String = "5") {

}

abstract class GeoLookup(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  override val radius: String = "5",
  override val oAuthSession: OAuthService)

  extends Lookup(oAuthSession, radius) {

}

class DBpediaLookup(
  // By default, search for Brussels
  val targetResource: String = "Brussels",
  override val radius: String = "5",
  override val oAuthSession: OAuthService)

  extends Lookup(oAuthSession, radius) {

}