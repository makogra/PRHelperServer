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

        println("payload = $payload")
        if (event == "pull_request") {
            //TODO revert if for early return
            val json = Json { ignoreUnknownKeys = true }
            val prEvent = json.decodeFromString<PRPayload>(payload)

            if (prEvent.action == "opened" || prEvent.action == "synchronize") {
                val diffFile = webhookHandlerService.handlePullRequest(prEvent)
                val response = GPTService().sendToGPT(file = diffFile)
                response.recommendations.forEach { recommendation -> println("recommendation = $recommendation") }
            }
        }
        call.respond(HttpStatusCode.OK)
    }
}