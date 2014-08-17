package org.dbpedia.media_extractor.media_provider_session

import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Response
import org.scribe.model.Verb

class FlickrMediaProviderOAuthSession(
  savedCredentialsFile: String = "/flickr.setup.properties",
  savedAccessTokenFile: String = "/flickr.accessToken.properties")

  extends MediaProviderOAuthSession[FlickrApi](
    myApi = new FlickrApi,
    targetLicenses = "4,5,7,8", // See detail on licenses below
    savedCredentialsFile,
    savedAccessTokenFile) {

  /* Licenses (from https://secure.flickr.com/services/api/flickr.photos.licenses.getInfo.html)
   * 
   *<license id="0" name="All Rights Reserved" url="" />
   *<license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
   *<license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
   *<license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
   *<license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
   *<license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
   *<license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
   *<license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
   *<license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
   * 
   */

  override val endPointRootUri = "https://api.flickr.com/services/rest/"
  override val maxResultsPerQuery = "30" // according to FlickrAPI's TOU
  override val termsOfUseUri = "https://secure.flickr.com/help/terms/"

}

object FlickrMediaProviderOAuthSession {

  def apply(
    savedCredentialsFile: String = "/flickr.setup.properties",
    savedAccessTokenFile: String = "/flickr.accessToken.properties") =

    new FlickrMediaProviderOAuthSession(
      savedCredentialsFile = savedCredentialsFile,
      savedAccessTokenFile = savedAccessTokenFile)

}

 
