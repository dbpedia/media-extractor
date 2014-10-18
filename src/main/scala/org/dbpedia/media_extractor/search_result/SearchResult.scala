package org.dbpedia.media_extractor.search_result

import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.sparql.vocabulary.FOAF

abstract class SearchResult {
  def getUri(): String
  def getProperty(): Property
}

case class FlickrSearchResult(
  pageUri: String)
  extends SearchResult {
  override def getUri() = pageUri
  override def getProperty() = FOAF.page
}

case class YouTubeSearchResult(
  videoPageUri: String)
  extends SearchResult {
  override def getUri() = videoPageUri
  override def getProperty() = FOAF.page
}

case class VimeoSearchResult(
  linkUri: String)
  extends SearchResult {
  override def getUri() = linkUri
  override def getProperty() = FOAF.page
}

