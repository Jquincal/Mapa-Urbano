package com.mapaurbano.application

import io.ktor.server.application.Application

fun Application.module() {
    configurePlugins()
    configureRouting()
}
