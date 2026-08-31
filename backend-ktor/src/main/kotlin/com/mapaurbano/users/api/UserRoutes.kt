package com.mapaurbano.users.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.userRoutes() {
    route("/users") {
        post("/register") {
            call.respondEndpointNotImplemented("registerUser")
        }

        post("/login") {
            call.respondEndpointNotImplemented("loginUser")
        }

        post("/logout") {
            call.respondEndpointNotImplemented("logoutUser")
        }

        get("/me") {
            call.respondEndpointNotImplemented("getCurrentUser")
        }

        get("/me/reports") {
            call.respondEndpointNotImplemented("listCurrentUserReports")
        }

        get("/me/reports/{id}") {
            call.respondEndpointNotImplemented("getCurrentUserReport")
        }
    }
}
