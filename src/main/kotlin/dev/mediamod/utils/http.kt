package dev.mediamod.utils

import kotlinx.serialization.encodeToString
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val client = HttpClient.newBuilder().build()

fun post(url: String, body: Map<String, String>): String {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .method("POST", HttpRequest.BodyPublishers.ofString(json.encodeToString(body)))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

fun get(url: String, headers: Map<String, String>): String {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")

    headers.forEach(request::setHeader)

    val response = client.send(request.build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}