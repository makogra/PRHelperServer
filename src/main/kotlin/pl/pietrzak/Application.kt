package pl.pietrzak

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import pl.pietrzak.services.GitHubApiService
import pl.pietrzak.services.WebhookHandlerService
import pl.pietrzak.entity.github.PRPayload
import pl.pietrzak.services.GPTService


fun main(args: Array<String>) {
//    io.ktor.server.netty.EngineMain.main(args)
    main2()
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureRouting()
}

fun main2() {
    embeddedServer(Netty, port = 8080, module = Application::mainModule).start(wait = true)
}

fun Application.mainModule() {
    val clientId = System.getenv("GITHUB_CLIENT_ID") ?: ""//error("GITHUB_CLIENT_ID not set")
    val clientSecret = System.getenv("GITHUB_CLIENT_SECRET") ?: ""//error("GITHUB_CLIENT_SECRET not set")

    install(CallLogging) {
        level = Level.INFO
    }

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    val httpClient = HttpClient(CIO) {
        install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    httpClient.monitor.subscribe(ApplicationStopping) {
        httpClient.close()
        log.info("HttpClient closed.")
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }

    val webhookHandlerService = WebhookHandlerService(httpClient)
    val gitHubApiService = GitHubApiService(clientId, clientSecret, httpClient)

    routing {
        get("/") {
            call.respondText("GitHub AI Code Review Backend is running.")
        }

        // OAuth callback endpoint
        get("/auth/github/callback") {
            val code = call.request.queryParameters["code"]
            if (code == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing 'code' parameter.")
                return@get
            }

            val tokenResponse = gitHubApiService.exchangeCodeForAccessToken(code)
            val userResponse = gitHubApiService.fetchAuthenticatedUser(tokenResponse)

            call.respond(userResponse)
        }

        // GitHub Webhook listener
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

                    println("Response: ${response.choices.firstOrNull()?.message?.content}")
//                    response.choices.firstOrNull()?.message?.content?
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}