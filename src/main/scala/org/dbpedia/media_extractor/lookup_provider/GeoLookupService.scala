package org.dbpedia.media_extractor.lookup_provider

import com.hp.hpl.jena.rdf.model.Model

abstract class GeoLookupService(
  // By default, search for Brussels
  val lat: String = "50.85",
  val lon: String = "4.35",
  override val radius: String = "5")

  extends LookupService(radius) {
  // TODO: complete this empty stub

  def performGeoLookup(lat: String = lat, lon: String = lon, radius: String = radius): Model
}
