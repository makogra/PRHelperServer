package pl.pietrzak.auth

import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.sessions.*

fun Routing.githubOAuthRoutes() {
    val clientId = System.getenv("GITHUB_CLIENT_ID") ?: error("Missing GITHUB_CLIENT_ID")
    val clientSecret = System.getenv("GITHUB_CLIENT_SECRET") ?: error("Missing GITHUB_CLIENT_SECRET")

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    get("/auth/login") {
        val redirectUri = "http://localhost:8080/auth/callback"
        val githubAuthUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=$clientId&redirect_uri=$redirectUri&scope=read:user"
        call.respondRedirect(githubAuthUrl)
    }

    get("/auth/callback") {
        val code = call.request.queryParameters["code"]
        if (code == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing code")
            return@get
        }

        val tokenResponse: GitHubTokenResponse = httpClient.post("https://github.com/login/oauth/access_token") {
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
            parameter("client_id", clientId)
            parameter("client_secret", clientSecret)
            parameter("code", code)
        }.body()

        val userResponse: GitHubUserResponse = httpClient.get("https://api.github.com/user") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${tokenResponse.accessToken}")
                append(HttpHeaders.Accept, "application/json")
            }
        }.body()

        call.sessions.set(AuthSession(tokenResponse.accessToken, userResponse.login))
        call.respondText("Logged in as ${userResponse.login}")
    }

    get("/auth/me") {
        val session = call.sessions.get<AuthSession>()
        if (session != null) {
            call.respondText("You're logged in as: ${session.username}")
        } else {
            call.respondText("Not logged in", status = HttpStatusCode.Unauthorized)
        }
    }

    get("/auth/logout") {
        call.sessions.clear<AuthSession>()
        call.respondText("Logged out")
    }
}
