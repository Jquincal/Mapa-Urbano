package com.mapaurbano.statistics.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.statisticsRoutes() {
    get("/statistics") {
        call.respondEndpointNotImplemented("getStatistics")
    }
}
