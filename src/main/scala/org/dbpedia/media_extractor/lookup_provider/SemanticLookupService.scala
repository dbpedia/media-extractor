package org.dbpedia.media_extractor.lookup_provider

import com.hp.hpl.jena.rdf.model.Model

trait SemanticLookupService

  extends LookupService {
  // TODO: complete this empty stub

  // By default, search for Brussels
  def performSemanticLookup(targetResource: String = "Brussels", radius: String = radius): Model
}
