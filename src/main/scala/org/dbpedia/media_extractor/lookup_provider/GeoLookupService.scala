package org.dbpedia.media_extractor.lookup_provider

import com.hp.hpl.jena.rdf.model.Model

trait GeoLookupService

  extends LookupService {
  // TODO: complete this empty stub

  // By default, search for Brussels
  def performGeoLookup(lat: String = "50.85", lon: String = "4.35", radius: String = radius): Model
}
