package pl.pietrzak.auth

import io.ktor.server.auth.*
import io.ktor.server.sessions.*

data class AuthSession(val accessToken: String, val username: String)
