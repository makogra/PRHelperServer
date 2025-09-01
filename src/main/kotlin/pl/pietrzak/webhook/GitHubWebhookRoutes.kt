package pl.pietrzak.webhook

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import pl.pietrzak.webhook.GitHubPullRequestPayload

fun Routing.githubWebhookRoute() {
    val client = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json()
        }
    }

    post("/webhook/github") {
        val event = call.request.header("X-GitHub-Event") ?: return@post call.respond(HttpStatusCode.BadRequest)
        if (event != "pull_request") return@post call.respond(HttpStatusCode.OK)

        val payload = call.receiveText()
        println("payload : $payload")
        val json = Json.decodeFromString<GitHubPullRequestPayload>(payload)

        if (json.action == "opened" || json.action == "synchronize") {
            val diffUrl = json.pullRequest.diffUrl
            val authToken = System.getenv("GITHUB_TOKEN") ?: ""

            val diffResponse: HttpResponse = client.get(diffUrl) {
                headers {
                    append(HttpHeaders.Accept, "application/vnd.github.v3.diff")
                    append(HttpHeaders.Authorization, "Bearer $authToken")
                    append(HttpHeaders.UserAgent, "KtorWebhookListener")
                }
            }

            val diffContent = diffResponse.bodyAsText()
            println("Fetched diff:\n$diffContent")

            // Pass diffContent to OpenAI / further processing
        }

        call.respond(HttpStatusCode.OK)
    }
}
