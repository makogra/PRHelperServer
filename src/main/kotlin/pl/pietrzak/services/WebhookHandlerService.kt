package pl.pietrzak.services

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import pl.pietrzak.entity.github.PRPayload

class WebhookHandlerService (
    private var client: HttpClient
) {
    suspend fun handlePullRequest(payload: PRPayload): String {
        //TODO verify if user is registered

        // fetch diff
        // wrap for massage to GPT API
        // recive and parse the response
        // save response to db
        // notify user

        val pr = payload.pullRequest
        val repo = payload.repository

        println("Received PR: ${pr.id} by ${pr.user.login} on ${repo.name}")

        val diff = fetchPullRequestDiff(diffUrl = pr.diffUrl)


        return diff
    }

    private suspend fun fetchPullRequestDiff(diffUrl: String, accessToken: String? = null): String {
        return client.get(diffUrl) {
            headers {
                if (accessToken != null) {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
                append(HttpHeaders.Accept, "application/vnd.github.v3.diff")
            }
        }.bodyAsText()
    }
}