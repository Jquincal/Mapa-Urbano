package com.mapaurbano.reports.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.adminReportRoutes() {
    route("/reports") {
        get {
            call.respondEndpointNotImplemented("listAdminReports")
        }

        get("/{id}") {
            call.respondEndpointNotImplemented("getAdminReport")
        }

        patch("/{id}/status") {
            call.respondEndpointNotImplemented("updateReportStatus")
        }

        patch("/{id}/priority") {
            call.respondEndpointNotImplemented("updateReportPriority")
        }

        delete("/{id}") {
            call.respondEndpointNotImplemented("deleteReport")
        }
    }
}
