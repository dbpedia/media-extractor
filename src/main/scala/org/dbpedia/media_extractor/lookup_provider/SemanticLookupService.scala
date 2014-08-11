package org.dbpedia.media_extractor.lookup_provider

import com.hp.hpl.jena.rdf.model.Model

abstract class SemanticLookupService(
  // By default, search for Brussels
  val targetResource: String = "Brussels",
  override val radius: String = "5")

  extends LookupService(radius) {
  // TODO: complete this empty stub

  def performSemanticLookup(targetResource: String = "Brussels", radius: String = radius): Model
}
