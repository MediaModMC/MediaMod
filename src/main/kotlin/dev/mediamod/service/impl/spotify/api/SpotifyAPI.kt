package dev.mediamod.service.impl.spotify.api

import org.apache.http.client.utils.URIBuilder
import java.net.URL

class SpotifyAPI(
    private val clientID: String
) {
    companion object {
        private const val baseURL = "accounts.spotify.com"
    }

    fun generateAuthorizationURL(scopes: String, redirectURI: String, state: String): URL =
        URIBuilder().apply {
            scheme = "https"
            host = baseURL
            path = "/authorize"
            addParameter("response_type", "code")
            addParameter("client_id", clientID)
            addParameter("scope", scopes)
            addParameter("redirect_uri", redirectURI)
            addParameter("state", state)
        }.build().toURL()
}