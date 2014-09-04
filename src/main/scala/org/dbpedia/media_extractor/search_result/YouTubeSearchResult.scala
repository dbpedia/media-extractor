package org.dbpedia.media_extractor.search_result

case class YouTubeSearchResult(
  thumbnailUri: String)
  extends SearchResult {
  def getLinks() = thumbnailUri
}
