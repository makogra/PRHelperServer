package pl.pietrzak.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import pl.pietrzak.auth.GitHubUser

class GitHubApiService(
    private val clientId: String,
    private val clientSecret: String,
    private val httpClient: HttpClient
) {

    suspend fun exchangeCodeForAccessToken(code: String): String {
        val response: HttpResponse = httpClient.post("https://github.com/login/oauth/access_token") {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(
                mapOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "code" to code
                )
            )
        }

        val tokenResponse: GitHubAccessTokenResponse = response.body()
        return tokenResponse.access_token
    }

    suspend fun fetchAuthenticatedUser(accessToken: String): GitHubUser {
        return httpClient.get("https://api.github.com/user") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.Accept, "application/vnd.github+json")
            }
        }.body()
    }

    suspend fun fetchPullRequestDiff(owner: String, repo: String, number: Int, accessToken: String): String {
        return httpClient.get("https://api.github.com/repos/$owner/$repo/pulls/$number") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.Accept, "application/vnd.github.v3.diff")
            }
        }.bodyAsText()
    }
}

@Serializable
data class GitHubAccessTokenResponse(
    val access_token: String,
    val scope: String,
    val token_type: String
)
