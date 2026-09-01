package com.mapaurbano.media.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.publicImageRoutes() {
    get("/reports/{id}/image") {
        call.respondEndpointNotImplemented("getPublicReportImage")
    }
}

fun Route.adminImageRoutes() {
    get("/reports/{id}/image") {
        call.respondEndpointNotImplemented("getAdminReportImage")
    }
}
