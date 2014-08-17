package org.dbpedia.media_extractor.oauthsession

import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class FlickrOAuthSession(
  savedCredentialsFile: String = "/flickr.setup.properties",
  savedAccessTokenFile: String = "/flickr.accessToken.properties")

  extends OAuthSession[FlickrApi](
    myProviderApi = new FlickrApi,
    savedCredentialsFile,
    savedAccessTokenFile) {

}

object FlickrMediaProviderOAuthSession {

  def apply(
    savedCredentialsFile: String = "/flickr.setup.properties",
    savedAccessTokenFile: String = "/flickr.accessToken.properties") =

    new FlickrOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)

}

 
