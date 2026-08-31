package com.mapaurbano.application

import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RoutingTest {
    @Test
    fun `liveness reports that the process is running`() = testApplication {
        application { module() }

        val response = client.request("/health/live")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "\"status\":\"up\"")
    }

    @Test
    fun `readiness remains unavailable until dependencies are configured`() = testApplication {
        application { module() }

        val response = client.request("/health/ready")

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
        assertContains(response.bodyAsText(), "\"status\":\"not_ready\"")
    }

    @Test
    fun `business endpoints are registered as explicit stubs`() = testApplication {
        application { module() }

        val endpoints = listOf(
            Endpoint(HttpMethod.Get, "/api/v1/categories"),
            Endpoint(HttpMethod.Post, "/api/v1/reports"),
            Endpoint(HttpMethod.Get, "/api/v1/reports/123"),
            Endpoint(HttpMethod.Post, "/api/v1/report-status"),
            Endpoint(HttpMethod.Post, "/api/v1/users/register"),
            Endpoint(HttpMethod.Post, "/api/v1/users/login"),
            Endpoint(HttpMethod.Post, "/api/v1/users/logout"),
            Endpoint(HttpMethod.Get, "/api/v1/users/me"),
            Endpoint(HttpMethod.Get, "/api/v1/users/me/reports"),
            Endpoint(HttpMethod.Get, "/api/v1/users/me/reports/123"),
            Endpoint(HttpMethod.Post, "/api/v1/auth/login"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/reports"),
            Endpoint(HttpMethod.Patch, "/api/v1/admin/reports/123/status"),
            Endpoint(HttpMethod.Put, "/api/v1/admin/reports/123/assignment"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/teams"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/assignees"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/categories"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/statistics"),
            Endpoint(HttpMethod.Get, "/api/v1/admin/audit"),
        )

        endpoints.forEach { endpoint ->
            val response = client.request(endpoint.path) {
                method = endpoint.method
            }

            assertEquals(
                HttpStatusCode.NotImplemented,
                response.status,
                "${endpoint.method.value} ${endpoint.path} no quedó registrada correctamente",
            )
            assertContains(response.bodyAsText(), "NOT_IMPLEMENTED")
        }
    }

    private data class Endpoint(
        val method: HttpMethod,
        val path: String,
    )
}
