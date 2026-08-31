package com.mapaurbano.auth.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    route("/auth") {
        post("/login") {
            call.respondEndpointNotImplemented("login")
        }

        post("/logout") {
            call.respondEndpointNotImplemented("logout")
        }

        get("/me") {
            call.respondEndpointNotImplemented("getCurrentAdmin")
        }
    }
}
