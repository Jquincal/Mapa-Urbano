package com.mapaurbano.assignments.api

import com.mapaurbano.shared.api.respondEndpointNotImplemented
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.teamRoutes() {
    route("/teams") {
        get {
            call.respondEndpointNotImplemented("listTeams")
        }

        post {
            call.respondEndpointNotImplemented("createTeam")
        }

        get("/{id}") {
            call.respondEndpointNotImplemented("getTeam")
        }

        patch("/{id}") {
            call.respondEndpointNotImplemented("updateTeam")
        }

        put("/{teamId}/members/{adminUserId}") {
            call.respondEndpointNotImplemented("addTeamMember")
        }

        delete("/{teamId}/members/{adminUserId}") {
            call.respondEndpointNotImplemented("removeTeamMember")
        }
    }

    get("/assignees") {
        call.respondEndpointNotImplemented("listAssignees")
    }
}
