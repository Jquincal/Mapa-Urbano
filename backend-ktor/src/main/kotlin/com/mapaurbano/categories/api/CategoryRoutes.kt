package com.mapaurbano.categories.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.publicCategoryRoutes() {
    get("/categories") {
        call.respondEndpointNotImplemented("listPublicCategories")
    }
}

fun Route.adminCategoryRoutes() {
    route("/categories") {
        get {
            call.respondEndpointNotImplemented("listAdminCategories")
        }

        post {
            call.respondEndpointNotImplemented("createCategory")
        }

        patch("/{id}") {
            call.respondEndpointNotImplemented("updateCategory")
        }
    }
}
