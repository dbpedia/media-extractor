package org.dbpedia.media_extractor.media_provider

import com.hp.hpl.jena.rdf.model.Model

abstract class GeoLookupService(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  override val radius: String = "5")

  extends LookupService(radius) {
  // TODO: complete this empty stub

  def performGeoLookup(lat: String = "50.85", lon: String = "4.35", radius: String = radius): Model
}
