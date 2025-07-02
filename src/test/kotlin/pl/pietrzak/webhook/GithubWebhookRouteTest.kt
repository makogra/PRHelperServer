package pl.pietrzak.webhook

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class FakePullRequestPayload(val action: String)

fun Application.testWebhookModule() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        post("/webhook/github") {
            val event = call.request.header("X-GitHub-Event")
            val payload = call.receive<FakePullRequestPayload>()
            if (event == "pull_request" && (payload.action == "opened" || payload.action == "synchronize")) {
                call.response.status(HttpStatusCode.OK);
            } else {
                call.response.status(HttpStatusCode.BadRequest);
            }
        }
    }
}

class GithubWebhookRouteTest {
    @Test
    fun testValidWebhook() = testApplication {
        application { testWebhookModule() }

        val response = client.post("/webhook/github") {
            header("X-GitHub-Event", "pull_request")
            contentType(ContentType.Application.Json)
            setBody("""{"action":"opened"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testInvalidEvent() = testApplication {
        application { testWebhookModule() }

        val response = client.post("/webhook/github") {
            header("X-GitHub-Event", "push")
            contentType(ContentType.Application.Json)
            setBody("""{"action":"opened"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

}