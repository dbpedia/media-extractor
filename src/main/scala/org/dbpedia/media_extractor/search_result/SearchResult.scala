package org.dbpedia.media_extractor.search_result

import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.rdf.model.Property

abstract class SearchResult {
  def getUri(): String
  def getProperty(): Property
}

case class FlickrPageSearchResult(
  pageUri: String)
  extends SearchResult {
  override def getUri() = pageUri
  override def getProperty() = FOAF.page
}

case class FlickrDepictionSearchResult(
  depictionUri: String)
  extends SearchResult {
  override def getUri() = depictionUri
  override def getProperty() = FOAF.depiction

}

case class FlickrSearchResult(
  page: FlickrPageSearchResult,
  depiction: FlickrDepictionSearchResult)
  extends SearchResult

case class YouTubeSearchResult(
  thumbnailUri: String)
  extends SearchResult {
  override def getUri() = thumbnailUri
  override def getProperty() = FOAF.page

}

