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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import pl.pietrzak.database.DatabaseFactory
import pl.pietrzak.database.repositories.ConversationRepository
import pl.pietrzak.services.GitHubApiService
import pl.pietrzak.routes.githubOAuthRoutes
import pl.pietrzak.routes.githubWebhookRoutes
import pl.pietrzak.services.WebhookHandlerService


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

    DatabaseFactory.init()
    val conversationRepository = ConversationRepository()

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

    routing {
        get("/") {
            call.respondText("GitHub AI Code Review Backend is running.")
        }

        githubWebhookRoutes(webhookHandlerService)
        githubOAuthRoutes()
    }
}