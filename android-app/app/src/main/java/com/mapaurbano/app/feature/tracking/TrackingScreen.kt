package com.mapaurbano.app.feature.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.StatusEvent
import com.mapaurbano.app.feature.common.MiniMapIllustration
import com.mapaurbano.app.feature.common.DefaultTopBarActions
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchPrimaryButton
import com.mapaurbano.app.feature.common.StitchSearchField
import com.mapaurbano.app.feature.common.StitchStatusPill

@Composable
fun TrackingScreen(
    query: String,
    report: Report?,
    errorMessage: String?,
    onQueryChange: (String) -> Unit,
    onTrack: () -> Unit,
    modifier: Modifier = Modifier,
    onReportClick: ((Report) -> Unit)? = null,
    isLoading: Boolean = false,
    onPaste: (() -> Unit)? = null,
    onProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
) {
    ScrollableFeatureScreen(
        title = "Seguimiento",
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 10.dp),
        topBarActions = {
            DefaultTopBarActions(
                onProfile = onProfile,
                onNotifications = onNotifications,
            )
        },
    ) {
        Text(
            text = "Consultar reporte",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        StitchSearchField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = "Ej.: B4K9-7M2Q-X8NV",
            trailing = {
                if (onPaste != null) {
                    IconButton(onClick = onPaste) {
                        Icon(Icons.Rounded.ContentPaste, contentDescription = "Pegar código")
                    }
                }
            },
        )
        StitchPrimaryButton(
            label = if (isLoading) "Consultando..." else "Consultar estado",
            onClick = onTrack,
            enabled = query.isNotBlank() && !isLoading,
            icon = Icons.Rounded.Search,
            content = if (isLoading) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Consultando...")
                }
            } else {
                null
            },
        )
        errorMessage?.let {
            StitchCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                Text("No encontramos el reporte", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(it, color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }

        if (report == null && errorMessage == null) {
            StitchCard(containerColor = MuniColors.SurfaceContainerLow) {
                Text("Código de seguimiento", fontWeight = FontWeight.SemiBold)
                Text(
                    "Aparece en la confirmación del reporte anónimo. Tiene 12 caracteres agrupados de a cuatro.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        report?.let {
            TrackingResult(report = it, onReportClick = onReportClick)
        }
    }
}

@Composable
private fun TrackingResult(report: Report, onReportClick: ((Report) -> Unit)?) {
    Column(
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StitchCard {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(report.id, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(report.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(5.dp))
                        Text(report.location, style = MaterialTheme.typography.bodySmall)
                    }
                }
                StitchStatusPill(report.status)
            }
            MiniMapIllustration(label = "Ubicación del reporte")
            if (onReportClick != null) {
                Button(onClick = { onReportClick(report) }, modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 46.dp)) {
                    Text("Ver detalle completo")
                }
            }
        }
        Text("Historial del reporte", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        StatusTimeline(events = report.history)
        StitchCard(containerColor = MuniColors.Orange100) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MuniColors.Orange700)
                Text("Recibir alerta cuando cambie el estado", color = MuniColors.Orange700, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun StatusTimeline(events: List<StatusEvent>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (events.isEmpty()) {
            Text("Todavía no hay novedades.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        events.forEachIndexed { index, event ->
            TimelineEvent(event = event, index = index, isLast = index == events.lastIndex)
        }
    }
}

@Composable
private fun TimelineEvent(event: StatusEvent, index: Int, isLast: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(if (index == 0) MuniColors.PrimaryBright else MuniColors.Green100, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = if (index == 0) MuniColors.White else MuniColors.Green700, modifier = Modifier.size(20.dp))
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .size(width = 2.dp, height = 58.dp)
                        .background(MuniColors.OutlineVariant),
                )
            }
        }
        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Text(event.dateLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
