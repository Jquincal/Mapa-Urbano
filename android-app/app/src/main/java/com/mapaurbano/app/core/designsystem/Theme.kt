package com.mapaurbano.app.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

/** MuniReport theme. Brand colours are deliberately stable across Android versions. */
@Composable
fun MapaUrbanoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) MuniDarkColorScheme else MuniLightColorScheme,
        typography = MuniTypography,
        shapes = androidx.compose.material3.Shapes(
            extraSmall = MuniShapes.Small,
            small = MuniShapes.Small,
            medium = MuniShapes.Medium,
            large = MuniShapes.Large,
            extraLarge = MuniShapes.Large,
        ),
        content = content,
    )
}

/** Alias useful when the product name is presented as MuniReport. */
@Composable
fun MuniReportTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) = MapaUrbanoTheme(darkTheme = darkTheme, content = content)

object MuniTheme {
    val spacing: MuniSpacing
        @Composable @ReadOnlyComposable get() = MuniSpacing

    val dimensions: MuniDimensions
        @Composable @ReadOnlyComposable get() = MuniDimensions

    val shapes: MuniShapes
        @Composable @ReadOnlyComposable get() = MuniShapes
}
