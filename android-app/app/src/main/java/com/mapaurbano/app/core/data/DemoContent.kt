package com.mapaurbano.app.core.data

import com.mapaurbano.app.core.model.Category
import com.mapaurbano.app.core.model.MapPoint
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ReportStatus
import com.mapaurbano.app.core.model.StatusEvent
import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.core.model.User

object DemoContent {
    val categories = listOf(
        Category("roads", "Baches y calzada", "minor_crash"),
        Category("lighting", "Alumbrado público", "light"),
        Category("waste", "Higiene y basura", "delete"),
        Category("public-space", "Espacio público", "park"),
        Category("obstruction", "Obstrucción vial", "block"),
        Category("signage", "Señalización", "traffic"),
        Category("water", "Agua y desagüe", "water_drop"),
        Category("other", "Otros servicios", "more_horiz"),
    )

    val user = User("demo-user", "Valeria Cifuentes", "valeria@correo.com")

    private fun category(id: String) = categories.first { it.id == id }

    val reports = listOf(
        Report(
            id = "REP-2048",
            title = "Bache profundo frente al cruce",
            description = "El pozo ocupa parte de la calzada y complica el paso de colectivos.",
            category = category("roads"),
            status = ReportStatus.InProgress,
            location = "San Martín y Rivadavia",
            point = MapPoint(.34f, .42f, -32.8891, -68.8448, "San Martín y Rivadavia"),
            createdAtLabel = "hace 2 h",
            submissionMode = SubmissionMode.Anonymous,
            trackingCode = "7F2K-9B1M-4X3P",
            hasPhoto = true,
            history = listOf(
                StatusEvent(ReportStatus.Pending, "Reporte recibido", "El municipio recibió el reporte.", "Hoy, 09:15"),
                StatusEvent(ReportStatus.InProgress, "En proceso", "El equipo responsable está revisando la incidencia.", "Hoy, 11:40"),
            ),
        ),
        Report(
            id = "REP-2047",
            title = "Luminaria intermitente en la plaza",
            description = "La luminaria queda apagada varios minutos y vuelve a encender.",
            category = category("lighting"),
            status = ReportStatus.Pending,
            location = "Plaza Central, lateral norte",
            point = MapPoint(.58f, .30f, -32.8882, -68.8462, "Plaza Central"),
            createdAtLabel = "ayer",
            submissionMode = SubmissionMode.Account,
            ownerId = user.id,
            history = listOf(StatusEvent(ReportStatus.Pending, "Reporte recibido", "Pendiente de revisión.", "Ayer, 18:20")),
        ),
        Report(
            id = "REP-2039",
            title = "Contenedor desbordado",
            description = "Hay residuos fuera del contenedor y obstaculizan la vereda.",
            category = category("waste"),
            status = ReportStatus.Resolved,
            location = "Barrio Centro, Manzana 4",
            point = MapPoint(.72f, .63f, -32.8904, -68.8428, "Barrio Centro"),
            createdAtLabel = "hace 3 días",
            submissionMode = SubmissionMode.Account,
            ownerId = user.id,
            hasPhoto = true,
            history = listOf(
                StatusEvent(ReportStatus.Pending, "Reporte recibido", "El reporte ingresó correctamente.", "28 sep, 09:10"),
                StatusEvent(ReportStatus.InProgress, "En proceso", "Se asignó un equipo.", "29 sep, 08:30"),
                StatusEvent(ReportStatus.Resolved, "Resuelto", "La incidencia fue atendida.", "30 sep, 16:45"),
            ),
        ),
        Report(
            id = "REP-2031",
            title = "Rama sobre senda peatonal",
            description = "Una rama baja obliga a bajar a la calle para pasar.",
            category = category("public-space"),
            status = ReportStatus.Pending,
            location = "Parque Belgrano, ingreso oeste",
            point = MapPoint(.44f, .74f, -32.8911, -68.8473, "Parque Belgrano"),
            createdAtLabel = "hace 5 días",
            submissionMode = SubmissionMode.Anonymous,
            trackingCode = "9PN4-2QRT-8W6A",
            history = listOf(StatusEvent(ReportStatus.Pending, "Reporte recibido", "Pendiente de revisión.", "27 sep, 12:05")),
        ),
    )
}
