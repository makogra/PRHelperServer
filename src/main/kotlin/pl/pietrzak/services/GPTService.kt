package pl.pietrzak.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import pl.pietrzak.openAi.OpenAiMessage
import pl.pietrzak.openAi.OpenAiRequest
import pl.pietrzak.openAi.OpenAiResponse

class GPTService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }
    suspend fun sendToGPT(
        command: String = """
            You are a helpful assistant reviewing code and providing feedback. When identifying issues or making recommendations, respond in the following format:
            1. **Type of recommendation/bug**: (e.g., rename, race condition, missed edge case)
            2. **Code snippet**: Provide the code fragment with the implementation of the solution, focusing on only the changed part to minimize token usage.
            3. **Explanation**: (optional, depending on the issue, e.g., skip for renaming) Provide a brief explanation of why this change is necessary.
        """.trimIndent(),
        file: String,
    ): OpenAiResponse {


        try {
            val apiKey = System.getenv("OPEN_AI_API_KEY")
            val orgId = System.getenv("OPEN_AI_ORGANIZATION_ID")
            val projectId = System.getenv("OPEN_AI_PROJECT_ID")

            val request = OpenAiRequest(
                model = "gpt-4o-mini",
                messages = listOf(
                    OpenAiMessage(role = "developer", content = command),
                    OpenAiMessage(role = "user", content = "Here is the code to review:\n$file")
                ),
                temperature = 0.7
            )

            val response: HttpResponse = client.post("https://api.openai.com/v1/chat/completions") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                    append("OpenAI-Organization", orgId)
                    append("OpenAI-Project", projectId)
                }
                setBody(request)
            }

            if (response.status.isSuccess()) {
                return response.body<OpenAiResponse>()
            } else {
                val errorBody: String = response.bodyAsText()
                throw Exception("API Error: ${response.status} - $errorBody")
            }
        } catch (e: Exception) {
            throw Exception("Failed to call OpenAI API: ${e.message}")
        } finally {
            client.close()
        }
    }
}