package com.mapaurbano.app.feature.map

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.Category
import com.mapaurbano.app.core.model.ContentLoadState
import com.mapaurbano.app.core.model.MapPresentation
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ReportFilters
import com.mapaurbano.app.core.model.ReportStatus
import com.mapaurbano.app.feature.common.ContentState
import com.mapaurbano.app.feature.common.ContentStatePanel
import com.mapaurbano.app.feature.common.DefaultTopBarActions
import com.mapaurbano.app.feature.common.FeatureTopBar
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchChip
import com.mapaurbano.app.feature.common.StitchMiniMap
import com.mapaurbano.app.feature.common.StitchReportCard
import com.mapaurbano.app.feature.common.StitchSearchField
import com.mapaurbano.app.feature.common.StitchStatusPill

@Composable
fun MapScreen(
    reports: List<Report>,
    categories: List<Category>,
    filters: ReportFilters,
    presentation: MapPresentation,
    modifier: Modifier = Modifier,
    contentLoadState: ContentLoadState = ContentLoadState.Ready,
    onQueryChange: (String) -> Unit,
    onPresentationChange: (MapPresentation) -> Unit,
    onStatusFilterChange: (ReportStatus?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onReportClick: (Report) -> Unit,
    onReportSelect: (Report) -> Unit = onReportClick,
    onCreateReport: () -> Unit,
    onRetry: () -> Unit = {},
    selectedReportId: String? = null,
    onProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onLocate: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FeatureTopBar(
                title = "Mapa",
                actions = {
                    DefaultTopBarActions(
                        onProfile = onProfile,
                        onNotifications = onNotifications,
                    )
                },
            )
        },
    ) { innerPadding ->
        when (contentLoadState) {
            ContentLoadState.Loading -> ContentStatePanel(
                state = ContentState.Loading,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            )

            ContentLoadState.Error -> ContentStatePanel(
                state = ContentState.Error("Revisá tu conexión e intentá nuevamente."),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                onRetry = onRetry,
            )

            ContentLoadState.Ready -> MapReadyContent(
                reports = reports,
                categories = categories,
                filters = filters,
                presentation = presentation,
                onQueryChange = onQueryChange,
                onPresentationChange = onPresentationChange,
                onStatusFilterChange = onStatusFilterChange,
                onCategoryFilterChange = onCategoryFilterChange,
                onClearFilters = onClearFilters,
                onReportClick = onReportClick,
                onReportSelect = onReportSelect,
                onCreateReport = onCreateReport,
                selectedReportId = selectedReportId,
                onLocate = onLocate,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun MapReadyContent(
    reports: List<Report>,
    categories: List<Category>,
    filters: ReportFilters,
    presentation: MapPresentation,
    onQueryChange: (String) -> Unit,
    onPresentationChange: (MapPresentation) -> Unit,
    onStatusFilterChange: (ReportStatus?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onReportClick: (Report) -> Unit,
    onReportSelect: (Report) -> Unit,
    onCreateReport: () -> Unit,
    selectedReportId: String?,
    onLocate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedReport = reports.firstOrNull { it.id == selectedReportId } ?: reports.firstOrNull()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StitchSearchField(
            value = filters.query,
            onValueChange = onQueryChange,
            placeholder = "Buscar calle, barrio o zona...",
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StitchChip(
                label = "Mapa",
                selected = presentation == MapPresentation.Map,
                icon = Icons.Rounded.Map,
                onClick = { onPresentationChange(MapPresentation.Map) },
            )
            StitchChip(
                label = "Lista",
                selected = presentation == MapPresentation.List,
                icon = Icons.Rounded.List,
                onClick = { onPresentationChange(MapPresentation.List) },
            )
            Spacer(Modifier.weight(1f))
            StitchChip(
                label = "Filtros",
                selected = filters.status != null || filters.categoryId != null,
                icon = Icons.Rounded.Tune,
                selectedColor = MuniColors.Orange,
                onClick = onClearFilters,
            )
        }
        FilterChips(
            filters = filters,
            categories = categories,
            onStatusFilterChange = onStatusFilterChange,
            onCategoryFilterChange = onCategoryFilterChange,
        )

        if (reports.isEmpty()) {
            EmptyMapResults(
                onClearFilters = onClearFilters,
                onCreateReport = onCreateReport,
            )
            return@Column
        }

        if (presentation == MapPresentation.Map) {
            Box {
                StitchMiniMap(
                    reports = reports,
                    selectedReportId = selectedReportId,
                    onReportClick = onReportSelect,
                    onLocate = onLocate,
                    modifier = Modifier.height(430.dp),
                )
                Button(
                    onClick = onCreateReport,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 14.dp, bottom = 72.dp)
                        .sizeIn(minHeight = 52.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(containerColor = MuniColors.PrimaryBright),
                    contentPadding = PaddingValues(horizontal = 18.dp),
                ) {
                    Icon(Icons.Rounded.AddLocationAlt, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Reportar problema", style = MaterialTheme.typography.labelLarge)
                }
            }
            selectedReport?.let {
                StitchMapSheet(report = it, onClick = { onReportClick(it) })
            }
            CivicStats(reports = reports)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${reports.size} reportes registrados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.semantics { heading() },
                )
                reports.forEach { report ->
                    StitchReportCard(report = report, onClick = { onReportClick(report) })
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    filters: ReportFilters,
    categories: List<Category>,
    onStatusFilterChange: (ReportStatus?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StitchChip("Todos", selected = filters.status == null, onClick = { onStatusFilterChange(null) })
        ReportStatus.entries.forEach { status ->
            StitchChip(
                label = status.label,
                selected = filters.status == status,
                selectedColor = statusAccent(status),
                onClick = { onStatusFilterChange(if (filters.status == status) null else status) },
            )
        }
        categories.take(4).forEach { category ->
            StitchChip(
                label = category.label,
                selected = filters.categoryId == category.id,
                selectedColor = MuniColors.Orange,
                onClick = { onCategoryFilterChange(if (filters.categoryId == category.id) null else category.id) },
            )
        }
    }
}

@Composable
private fun StitchMapSheet(report: Report, onClick: () -> Unit) {
    StitchCard {
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(report.category.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(report.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(report.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StitchStatusPill(report.status)
        }
        Text(report.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 46.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text("Ver detalle completo")
        }
    }
}

@Composable
private fun CivicStats(reports: List<Report>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(
            "Activos" to reports.count { it.status != ReportStatus.Resolved }.toString(),
            "Resueltos" to reports.count { it.status == ReportStatus.Resolved }.toString(),
            "Zonas" to reports.map { it.location }.distinct().size.toString(),
        ).forEach { (label, value) ->
            StitchCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(10.dp), containerColor = MuniColors.SurfaceContainerLow) {
                Text(value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MapReportCard(report: Report, onClick: () -> Unit, modifier: Modifier = Modifier) {
    StitchReportCard(report = report, onClick = onClick, modifier = modifier)
}

@Composable
fun ReportStatusBadge(status: ReportStatus, modifier: Modifier = Modifier) {
    StitchStatusPill(status = status, modifier = modifier)
}

@Composable
private fun EmptyMapResults(onClearFilters: () -> Unit, onCreateReport: () -> Unit) {
    StitchCard(containerColor = MuniColors.SurfaceContainerLow) {
        Text("No encontramos reportes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text("Probá cambiando la búsqueda o creando un nuevo reporte vecinal.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onClearFilters, modifier = Modifier.weight(1f)) { Text("Limpiar") }
            Button(onClick = onCreateReport, modifier = Modifier.weight(1f)) { Text("Reportar") }
        }
    }
}

private fun statusAccent(status: ReportStatus): Color = when (status) {
    ReportStatus.Pending -> MuniColors.Orange
    ReportStatus.InProgress -> MuniColors.PrimaryBright
    ReportStatus.Resolved -> MuniColors.Green700
}
