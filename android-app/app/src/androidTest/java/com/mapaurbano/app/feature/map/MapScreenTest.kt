package com.mapaurbano.app.feature.map

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.mapaurbano.app.core.data.DemoContent
import com.mapaurbano.app.core.designsystem.MapaUrbanoTheme
import com.mapaurbano.app.core.model.MapPresentation
import com.mapaurbano.app.core.model.ReportFilters
import org.junit.Rule
import org.junit.Test

class MapScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun mapShowsPublicReportsAndCreateAction() {
        composeRule.setContent {
            MapaUrbanoTheme {
                MapScreen(
                    reports = DemoContent.reports,
                    categories = DemoContent.categories,
                    filters = ReportFilters(),
                    presentation = MapPresentation.Map,
                    onQueryChange = {},
                    onPresentationChange = {},
                    onStatusFilterChange = {},
                    onCategoryFilterChange = {},
                    onClearFilters = {},
                    onReportClick = {},
                    onCreateReport = {},
                )
            }
        }

        composeRule.onNodeWithText("Mapa urbano").assertExists()
        composeRule.onNodeWithText("Crear un reporte").assertExists()
        composeRule.onNodeWithText("Bache profundo frente al cruce").assertExists()
    }
}
