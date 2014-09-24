package org.dbpedia.media_extractor.oauthsession

import org.scalatest.FunSpec

class YouTubeOAuthSessionTest extends FunSpec {

 describe("A YouTubeOAuthSession instance") {

    it("should be able to connect to YouTube using saved credentials (only <ApiKey,ApiKeySecret>)") {
      val manuallyGeneratedYouTubeOAuthSession = YouTubeOAuthSession(
        savedCredentialsFile = "/youtube.setup.properties",
        savedAccessTokenFile = "")
      assert(!manuallyGeneratedYouTubeOAuthSession.accessToken.isEmpty())
    }

    it("should be able to connect to YouTube using saved credentials (both <ApiKey,ApiKeySecret> and <accessToken,accessTokenSecret>)") {
      val automaticallyGeneratedYouTubeOAuthSession = YouTubeOAuthSession(
        savedCredentialsFile = "/youtube.setup.properties",
        savedAccessTokenFile = "/youtube.accessToken.properties")

      val generatedToken = automaticallyGeneratedYouTubeOAuthSession.accessToken
      val savedToken = automaticallyGeneratedYouTubeOAuthSession.getSavedAccessToken("/youtube.accessToken.properties")
      assert(generatedToken === savedToken)
    }

  }

}
