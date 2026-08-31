package com.mapaurbano.reports.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.publicReportRoutes() {
    route("/reports") {
        get {
            call.respondEndpointNotImplemented("listPublicReports")
        }

        post {
            call.respondEndpointNotImplemented("createReport")
        }

        get("/{id}") {
            call.respondEndpointNotImplemented("getPublicReport")
        }
    }

    post("/report-status") {
        call.respondEndpointNotImplemented("getReportStatus")
    }
}
