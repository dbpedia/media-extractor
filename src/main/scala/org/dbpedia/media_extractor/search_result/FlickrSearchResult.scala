package org.dbpedia.media_extractor.search_result

case class FlickrSearchResult(
  depictionUri: String,
  pageUri: String)
  extends SearchResult {
  // for now, we will ignore "depictionUri"
  def getLinks() = pageUri
}
