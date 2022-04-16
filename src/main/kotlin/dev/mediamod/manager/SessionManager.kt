package dev.mediamod.manager

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import dev.mediamod.utils.Session
import dev.mediamod.utils.json
import dev.mediamod.utils.hex
import kotlinx.serialization.encodeToString
import java.security.MessageDigest
import java.security.SecureRandom

class SessionManager {
    private val constant = "82074fcd6eef4cafbc954dac50485fb7".encodeToByteArray()
    private val baseURL = "https://sessionserver.mojang.com"

    fun joinServer(): ByteArray? {
        val sharedSecret = generateSharedSecret()
        val serverIdHash = generateServerIdHash(sharedSecret)

        val body = mapOf(
            "accessToken" to Session.accessToken,
            "selectedProfile" to Session.uuid,
            "serverId" to serverIdHash
        )

        val (_, response, _) = Fuel.post("${baseURL}/session/minecraft/join")
            .jsonBody(json.encodeToString(body))
            .response()

        if (response.statusCode != 204)
            return null

        return sharedSecret
    }

    private fun generateServerIdHash(secret: ByteArray): String {
        val id = secret + constant
        return sha1(id).hex()
    }

    private fun generateSharedSecret(): ByteArray {
        val random = SecureRandom()
        val bytes = ByteArray(16)

        random.nextBytes(bytes)
        return bytes
    }

    private fun sha1(input: ByteArray): ByteArray {
        val sha1 = MessageDigest.getInstance("SHA-1")
        return sha1.digest(input)
    }
}