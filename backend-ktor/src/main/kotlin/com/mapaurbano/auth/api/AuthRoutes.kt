package com.mapaurbano.auth.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.adminAuthRoutes() {
    route("/auth") {
        post("/login") {
            call.respondEndpointNotImplemented("loginAdmin")
        }

        post("/logout") {
            call.respondEndpointNotImplemented("logoutAdmin")
        }

        get("/me") {
            call.respondEndpointNotImplemented("getCurrentAdmin")
        }
    }
}
