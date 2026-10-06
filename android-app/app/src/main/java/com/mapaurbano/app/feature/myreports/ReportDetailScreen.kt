package com.mapaurbano.app.feature.myreports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.StatusEvent
import com.mapaurbano.app.feature.common.NoticeCard
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.SectionTitle

@Composable
fun ReportDetailScreen(
    report: Report,
    modifier: Modifier = Modifier,
    showPrivateInformation: Boolean = false,
    message: String? = null,
    onBack: () -> Unit,
    onViewOnMap: (Report) -> Unit,
    onShare: ((Report) -> Unit)? = null,
) {
    ScrollableFeatureScreen(
        title = if (showPrivateInformation) "Detalle de mi reporte" else "Detalle del reporte",
        modifier = modifier,
        onBack = onBack,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = report.status.label,
                style = MaterialTheme.typography.labelLarge,
                color = statusColor(report.status),
            )
            Text(
                text = report.title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
            )
            if (showPrivateInformation) {
                Text(
                    text = "Identificador ${report.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (!message.isNullOrBlank()) NoticeCard(message)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                DetailRow(Icons.Rounded.Category, "Categoría", report.category.label)
                DetailRow(Icons.Rounded.LocationOn, "Ubicación", report.location)
                DetailRow(Icons.Rounded.CalendarToday, "Creado", report.createdAtLabel)
                if (showPrivateInformation) {
                    DetailRow(
                        Icons.Rounded.CheckCircle,
                        "Forma de envío",
                        report.submissionMode.label,
                    )
                }
            }
        }

        SectionTitle("Descripción")
        Text(report.description, style = MaterialTheme.typography.bodyLarge)

        if (report.hasPhoto) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.large,
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Image,
                        contentDescription = "Foto adjunta al reporte",
                        modifier = Modifier.size(48.dp),
                    )
                    Text("Foto adjunta", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        SectionTitle(
            title = "Historial",
            supportingText = "Actualizaciones registradas por el municipio.",
        )
        if (report.history.isEmpty()) {
            NoticeCard("Todavía no hay actualizaciones para este reporte.")
        } else {
            report.history.forEachIndexed { index, event ->
                TimelineEvent(
                    event = event,
                    isLatest = index == report.history.lastIndex,
                )
            }
        }

        Button(
            onClick = { onViewOnMap(report) },
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp),
        ) {
            Icon(Icons.Rounded.Map, contentDescription = null)
            Text("Ver ubicación en el mapa", modifier = Modifier.padding(start = 8.dp))
        }
        if (onShare != null) {
            OutlinedButton(
                onClick = { onShare(report) },
                modifier = Modifier
                    .fillMaxWidth()
                    .sizeIn(minHeight = 48.dp),
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null)
                Text("Compartir reporte", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun TimelineEvent(event: StatusEvent, isLatest: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (isLatest) statusColor(event.status) else MaterialTheme.colorScheme.outlineVariant,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isLatest) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Estado más reciente",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = event.dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(event.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
