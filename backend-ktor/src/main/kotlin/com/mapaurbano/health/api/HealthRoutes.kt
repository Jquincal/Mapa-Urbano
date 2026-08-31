package com.mapaurbano.health.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable

@Serializable
private data class HealthResponse(
    val status: String,
    val checks: Map<String, String> = emptyMap(),
)

fun Route.healthRoutes() {
    get("/health/live") {
        call.respond(HealthResponse(status = "up"))
    }

    get("/health/ready") {
        call.respond(
            HttpStatusCode.ServiceUnavailable,
            HealthResponse(
                status = "not_ready",
                checks = mapOf("database" to "not_configured"),
            ),
        )
    }
}
