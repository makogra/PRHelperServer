package pl.pietrzak.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import pl.pietrzak.openAi.RecommendationsResponse
import java.io.File

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
    ): RecommendationsResponse {


        try {
            val apiKey = System.getenv("OPEN_AI_API_KEY")
            val orgId = System.getenv("OPEN_AI_ORGANIZATION_ID")
            val projectId = System.getenv("OPEN_AI_PROJECT_ID")

            val request = buildRequest(file)
            val response: HttpResponse = client.post("https://api.openai.com/v1/responses") {
                contentType(ContentType.Application.Json)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                    append("OpenAI-Organization", orgId)
                    append("OpenAI-Project", projectId)
                }
                setBody(Json.encodeToJsonElement(request))
            }

            if (response.status.isSuccess()) {
                val apiResponse = response.body<JsonObject>()
                val outputText = apiResponse["output"]!!
                    .jsonArray[0].jsonObject["content"]!!
                    .jsonArray[0].jsonObject["text"]!!
                    .jsonPrimitive.content
                return Json.decodeFromString<RecommendationsResponse>(outputText)
            } else {
                val errorBody: String = response.bodyAsText()
                throw Exception("API Error: ${response.status} - $errorBody")
            }
        } catch (e: NullPointerException) {
            println("Open AI response is in different format than expected.")
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Failed to call OpenAI API: ${e.message}")
        } finally {
            client.close()
        }
    }

    fun loadJson(path: String): JsonObject =
        Json.parseToJsonElement(File(path).readText()).jsonObject

    fun buildRequest(fileContent: String): JsonObject {
        val requestTemplate = loadJson("src/main/resources/json_requests/code_review_request.json")
        val schema = loadJson("src/main/resources/json_schemas/code_review_schema.json")

        val input = requestTemplate["input"]!!.jsonArray.map { item ->
            val content = item.jsonObject["content"]!!.jsonPrimitive.content
                .replace("{{CODE}}", fileContent)
            buildJsonObject {
                put("role", item.jsonObject["role"]!!.jsonPrimitive.content)
                put("content", content)
            }
        }

        val responseFormat = buildJsonObject {
            put("type", "json_schema")
            put("strict", true)
            put("name", "PRHelperResponse")
            put("schema", schema)
        }

        val text = buildJsonObject {
            put("format", responseFormat)
        }

        return buildJsonObject {
            put("model", requestTemplate["model"]!!.jsonPrimitive.content)
            put("input", JsonArray(input))
            put("text", text)
        }

        //TODO make config fully in json
    }
}