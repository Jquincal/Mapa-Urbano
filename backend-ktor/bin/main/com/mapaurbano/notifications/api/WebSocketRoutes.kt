package com.mapaurbano.notifications.api

import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close

fun Route.webSocketRoutes() {
    webSocket("/ws/public") {
        close(
            CloseReason(
                CloseReason.Codes.TRY_AGAIN_LATER,
                "Canal público pendiente de implementación",
            ),
        )
    }

    webSocket("/ws/admin") {
        close(
            CloseReason(
                CloseReason.Codes.TRY_AGAIN_LATER,
                "Canal administrativo pendiente de implementación",
            ),
        )
    }
}
