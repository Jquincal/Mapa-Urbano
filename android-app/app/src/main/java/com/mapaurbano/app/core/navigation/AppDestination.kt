package com.mapaurbano.app.core.navigation

sealed interface AppDestination {
    data object Map : AppDestination
    data object CreateReport : AppDestination
    data object MyReports : AppDestination
    data object Tracking : AppDestination
    data object Profile : AppDestination
    data class Login(val returnTo: AppDestination) : AppDestination
    data class Register(val returnTo: AppDestination) : AppDestination
    data class ReportDetail(val reportId: String) : AppDestination
    data class Confirmation(val reportId: String) : AppDestination
    data object LocationPicker : AppDestination
}

val AppDestination.isMainDestination: Boolean
    get() = this === AppDestination.Map ||
        this === AppDestination.CreateReport ||
        this === AppDestination.MyReports ||
        this === AppDestination.Tracking
