package com.mapaurbano.app.core.model

enum class ReportStatus(val label: String) {
    Pending("Pendiente"),
    InProgress("En proceso"),
    Resolved("Resuelto"),
}

enum class SubmissionMode(val label: String) {
    Account("Guardar en mi cuenta"),
    Anonymous("Reportar de forma anónima"),
}

data class Category(
    val id: String,
    val label: String,
    val icon: String,
)

data class MapPoint(
    val x: Float,
    val y: Float,
    val latitude: Double,
    val longitude: Double,
    val label: String,
)

data class User(
    val id: String,
    val displayName: String,
    val email: String,
)

data class Report(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    val status: ReportStatus,
    val location: String,
    val point: MapPoint,
    val createdAtLabel: String,
    val submissionMode: SubmissionMode,
    val ownerId: String? = null,
    val trackingCode: String? = null,
    val hasPhoto: Boolean = false,
    val history: List<StatusEvent> = emptyList(),
)

data class StatusEvent(
    val status: ReportStatus,
    val title: String,
    val description: String,
    val dateLabel: String,
)

data class ReportDraft(
    val title: String = "",
    val description: String = "",
    val categoryId: String? = null,
    val location: String = "Av. San Martín 1420",
    val point: MapPoint = DefaultDraftPoint,
    val submissionMode: SubmissionMode = SubmissionMode.Anonymous,
    val hasPhoto: Boolean = false,
    val photoIssue: PhotoIssue? = null,
) {
    val isDirty: Boolean
        get() = title.isNotBlank() ||
            description.isNotBlank() ||
            categoryId != null ||
            hasPhoto ||
            photoIssue != null ||
            submissionMode != SubmissionMode.Anonymous ||
            location != "Av. San Martín 1420" ||
            point != DefaultDraftPoint

    val canSubmit: Boolean
        get() = title.trim().length >= 5 && categoryId != null && location.isNotBlank()
}

val DefaultDraftPoint = MapPoint(
    x = 0.50f,
    y = 0.52f,
    latitude = -32.8895,
    longitude = -68.8458,
    label = "Av. San Martín 1420",
)

data class ReportFilters(
    val query: String = "",
    val status: ReportStatus? = null,
    val categoryId: String? = null,
)

enum class MapPresentation { Map, List }

enum class ContentLoadState { Ready, Loading, Error }

enum class PhotoIssue(val message: String) {
    InvalidType("El archivo debe ser JPG, PNG o WebP."),
    TooLarge("La foto supera el máximo de 5 MB."),
    PermissionDenied("No se concedió acceso a la cámara. Podés elegir un archivo de ejemplo."),
}
