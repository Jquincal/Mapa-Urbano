package com.mapaurbano.app.feature.myreports

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.Category
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ReportFilters
import com.mapaurbano.app.core.model.ReportStatus
import com.mapaurbano.app.feature.common.ContentState
import com.mapaurbano.app.feature.common.ContentStatePanel
import com.mapaurbano.app.feature.common.DefaultTopBarActions
import com.mapaurbano.app.feature.common.FeatureTopBar
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchChip
import com.mapaurbano.app.feature.common.StitchReportCard
import com.mapaurbano.app.feature.common.StitchSearchField

@Composable
fun MyReportsScreen(
    reports: List<Report>,
    categories: List<Category>,
    filters: ReportFilters,
    modifier: Modifier = Modifier,
    contentState: ContentState = ContentState.Ready,
    onQueryChange: (String) -> Unit,
    onStatusFilterChange: (ReportStatus?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onReportClick: (Report) -> Unit,
    onCreateReport: () -> Unit,
    onRetry: () -> Unit = {},
    onProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
) {
    val visibleReports = reports.filter { report ->
        val query = filters.query.trim()
        (filters.status == null || report.status == filters.status) &&
            (filters.categoryId == null || report.category.id == filters.categoryId) &&
            (query.isEmpty() || listOf(report.title, report.description, report.location, report.category.label)
                .any { it.contains(query, ignoreCase = true) })
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FeatureTopBar(
                title = "Mis reportes",
                actions = {
                    DefaultTopBarActions(
                        onProfile = onProfile,
                        onNotifications = onNotifications,
                    )
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateReport,
                containerColor = MuniColors.Orange,
                contentColor = MuniColors.OnSurface,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Crear reporte")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .padding(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StitchSearchField(
                value = filters.query,
                onValueChange = onQueryChange,
                placeholder = "Buscar en mis reportes...",
                leadingIcon = Icons.Rounded.Search,
            )
            FilterStrip(
                filters = filters,
                categories = categories,
                onStatusFilterChange = onStatusFilterChange,
                onCategoryFilterChange = onCategoryFilterChange,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Reportes guardados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        text = "${visibleReports.size} visibles de ${reports.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (filters.query.isNotBlank() || filters.status != null || filters.categoryId != null) {
                    TextButton(onClick = onClearFilters, modifier = Modifier.sizeIn(minHeight = 44.dp)) {
                        Text("Limpiar")
                    }
                }
            }

            when {
                contentState != ContentState.Ready -> ContentStatePanel(state = contentState, onRetry = onRetry)
                visibleReports.isEmpty() -> StitchCard(containerColor = MuniColors.SurfaceContainerLow) {
                    Text("Todavía no hay reportes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Cuando registres una incidencia con tu cuenta, va a aparecer acá.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                else -> visibleReports.forEach { report ->
                    StitchReportCard(report = report, onClick = { onReportClick(report) }, showActions = true)
                }
            }
        }
    }
}

@Composable
private fun FilterStrip(
    filters: ReportFilters,
    categories: List<Category>,
    onStatusFilterChange: (ReportStatus?) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StitchChip("Todos", filters.status == null, onClick = { onStatusFilterChange(null) })
            ReportStatus.entries.forEach { status ->
                StitchChip(
                    label = status.label,
                    selected = filters.status == status,
                    selectedColor = when (status) {
                        ReportStatus.Pending -> MuniColors.Orange
                        ReportStatus.InProgress -> MuniColors.PrimaryBright
                        ReportStatus.Resolved -> MuniColors.Green700
                    },
                    onClick = { onStatusFilterChange(if (filters.status == status) null else status) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StitchChip("Todas", filters.categoryId == null, onClick = { onCategoryFilterChange(null) })
            categories.forEach { category ->
                StitchChip(
                    label = category.label,
                    selected = filters.categoryId == category.id,
                    selectedColor = MuniColors.Orange,
                    onClick = { onCategoryFilterChange(if (filters.categoryId == category.id) null else category.id) },
                )
            }
        }
    }
}

@Composable
internal fun statusColor(status: ReportStatus) = when (status) {
    ReportStatus.Pending -> MaterialTheme.colorScheme.error
    ReportStatus.InProgress -> MaterialTheme.colorScheme.primary
    ReportStatus.Resolved -> MaterialTheme.colorScheme.tertiary
}
