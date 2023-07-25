package com.poisonedyouth.financemanagement.e2e

import com.poisonedyouth.financemanagement.util.KtorServerTestExtension
import com.poisonedyouth.financemanagement.util.basicAuthHeader
import com.poisonedyouth.financemanagement.util.extractPassword
import com.poisonedyouth.financemanagement.util.extractUserId
import com.poisonedyouth.financemanagement.util.userIdRegex
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.http.parameters
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testcontainers.junit.jupiter.Testcontainers

@ExtendWith(KtorServerTestExtension::class)
@Testcontainers
class UserE2ETest {

    @Test
    fun `creation of new user and get user is working`() = runTest {
        // given
        val client = createHttpClient()
        val body = """
                    {
                        "firstname": "Hans",
                        "lastname": "Schmitt",
                        "email": "hans.schmitt@mail.com"
                    }
        """.trimIndent()

        // when
        val response = client.post("http://localhost:8080/api/v1/user") {
            setBody(body)
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }

        // then
        response.status shouldBe HttpStatusCode.Created
        response.bodyAsText() shouldContain "\"password\" :"
        response.bodyAsText() shouldContain "\"userId\" :"

        // Get user
        val userId = extractUserId(response)
        val password = extractPassword(response)

        val existingUserResponse = client.get("http://localhost:8080/api/v1/user") {
            parameters {
                parameter("userId", userId)
            }
            headers {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Authorization, basicAuthHeader(userId, password))
            }
        }

        //
        existingUserResponse.status shouldBe HttpStatusCode.OK
        val resultBody = existingUserResponse.bodyAsText()
        resultBody shouldContain (userIdRegex)
        resultBody shouldContain " \"firstname\" : \"Hans\","
        resultBody shouldContain " \"lastname\" : \"Schmitt\","
        resultBody shouldContain " \"email\" : \"hans.schmitt@mail.com"
    }

    private fun createHttpClient(): HttpClient {
        val client = HttpClient(CIO) {
        }
        return client
    }
}
