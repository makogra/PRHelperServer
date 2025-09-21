package pl.pietrzak.routes

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import pl.pietrzak.auth.AuthSession
import pl.pietrzak.auth.GitHubTokenResponse
import pl.pietrzak.auth.GitHubUserResponse

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