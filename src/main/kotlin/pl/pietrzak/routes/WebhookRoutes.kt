package pl.pietrzak.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import pl.pietrzak.entity.github.PRPayload
import pl.pietrzak.services.GPTService
import pl.pietrzak.services.WebhookHandlerService

fun Route.githubWebhookRoutes(webhookHandlerService: WebhookHandlerService) {
    post("/webhook/github") {
        val event = call.request.header("X-GitHub-Event")
        val payload = call.receiveText()

        if (event == "pull_request") {
            val json = Json { ignoreUnknownKeys = true }
            val prEvent = json.decodeFromString<PRPayload>(payload)

            if (prEvent.action == "opened" || prEvent.action == "synchronize") {
                val diffFile = webhookHandlerService.handlePullRequest(prEvent)
                val response = GPTService().sendToGPT(file = diffFile)

                println("Response: ${response.choices.firstOrNull()?.message?.content}")
            }
        }
        call.respond(HttpStatusCode.OK)
    }
}