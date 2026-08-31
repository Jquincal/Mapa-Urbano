package com.mapaurbano.assignments.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.put

fun Route.assignmentRoutes() {
    put("/reports/{id}/assignment") {
        call.respondEndpointNotImplemented("upsertReportAssignment")
    }

    delete("/reports/{id}/assignment") {
        call.respondEndpointNotImplemented("deleteReportAssignment")
    }
}
