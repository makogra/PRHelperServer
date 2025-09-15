package pl.pietrzak.services

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

//import org.junit.jupiter.api.Test

class GitHubApiServiceTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun createMockClient(response: HttpResponseData): HttpClient {
        return HttpClient(MockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
            engine {
                addHandler { _ -> response }
            }
        }
    }

    @Test
    fun `exchangeCodeForAccessToken should return access token`() = runBlocking {
        val tokenJson = """
            {
                "access_token": "test_token",
                "scope": "read:user",
                "token_type": "bearer"
            }
        """.trimIndent()

        val mockResponse = HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = GMTDate(),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
            version = HttpProtocolVersion.HTTP_1_1,
            body = ByteReadChannel(tokenJson),
            callContext = Job()
        )

        val client = createMockClient(mockResponse)
        val service = GitHubApiService("clientId", "clientSecret", client)

        val token = service.exchangeCodeForAccessToken("dummy_code")
        assertEquals("test_token", token)
    }

    @Test
    fun `fetchAuthenticatedUser should return GitHub user`() = runBlocking {
        val userJson = """
            {
                "login": "testuser",
                "id": 123456,
                "avatar_url": "https://avatars.githubusercontent.com/u/123456?v=4"
            }
        """.trimIndent()

        val mockResponse = HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = GMTDate(),
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
            version = HttpProtocolVersion.HTTP_1_1,
            body = ByteReadChannel(userJson),
            callContext = Job()
        )

        val client = createMockClient(mockResponse)
        val service = GitHubApiService("clientId", "clientSecret", client)

        val user = service.fetchAuthenticatedUser("token")
        assertEquals("testuser", user.login)
        assertEquals(123456, user.id)
    }

    @Test
    fun `fetchPullRequestDiff should return diff string`() = runBlocking {
        val diffText = """
            diff --git a/file.txt b/file.txt
            index abcdef..123456 100644
            --- a/file.txt
            +++ b/file.txt
            @@ -1 +1,2 @@
            -old line
            +new line
        """.trimIndent()

        val mockResponse = HttpResponseData(
            statusCode = HttpStatusCode.OK,
            requestTime = GMTDate(),
            headers = headersOf(HttpHeaders.ContentType, "text/plain"),
            version = HttpProtocolVersion.HTTP_1_1,
            body = ByteReadChannel(diffText),
            callContext = Job()
        )

        val client = createMockClient(mockResponse)
        val service = GitHubApiService("clientId", "clientSecret", client)

        val diff = service.fetchPullRequestDiff("owner", "repo", 1, "token")
        assertTrue(diff.contains("diff --git"))
    }
}