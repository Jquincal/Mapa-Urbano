package com.mapaurbano.app.core.designsystem

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Colores extraídos de las pantallas originales de Stitch. */
object MuniColors {
    val Primary = Color(0xFF0043C0)
    val PrimaryContainer = Color(0xFF0D59F2)
    val PrimaryFixed = Color(0xFFDCE1FF)
    val PrimaryFixedDim = Color(0xFFB5C4FF)
    val OnPrimaryFixed = Color(0xFF00164E)
    val OnPrimaryFixedVariant = Color(0xFF003CAD)

    val Secondary = Color(0xFF904D00)
    val SecondaryContainer = Color(0xFFFE932C)
    val SecondaryFixed = Color(0xFFFFDCC3)
    val SecondaryFixedDim = Color(0xFFFFB77D)
    val OnSecondaryContainer = Color(0xFF663500)

    val Tertiary = Color(0xFF005D28)
    val TertiaryContainer = Color(0xFF027836)
    val TertiaryFixed = Color(0xFF95F8A7)
    val TertiaryFixedDim = Color(0xFF79DB8D)

    val Error = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)

    val Background = Color(0xFFF9F9FF)
    val Surface = Color(0xFFF9F9FF)
    val SurfaceLowest = Color(0xFFFFFFFF)
    val SurfaceLow = Color(0xFFF0F3FF)
    val SurfaceContainer = Color(0xFFE7EEFF)
    val SurfaceHigh = Color(0xFFDEE8FF)
    val SurfaceHighest = Color(0xFFD8E3FB)
    val SurfaceDim = Color(0xFFCFDAF2)

    val OnSurface = Color(0xFF111C2D)
    val OnSurfaceVariant = Color(0xFF434655)
    val Outline = Color(0xFF737687)
    val OutlineVariant = Color(0xFFC3C5D8)
    val White = Color.White

    val PrimaryBright = PrimaryContainer
    val Orange = SecondaryContainer
    val OrangeDark = Secondary
    val Orange100 = SecondaryFixed
    val Orange700 = Secondary
    val Green700 = TertiaryContainer
    val Green100 = TertiaryFixed
    val SurfaceContainerLowest = SurfaceLowest
    val SurfaceContainerLow = SurfaceLow
    val SurfaceContainerHigh = SurfaceHigh
    val SurfaceContainerHighest = SurfaceHighest
}

internal val MuniLightColorScheme = lightColorScheme(
    primary = MuniColors.Primary,
    onPrimary = MuniColors.White,
    primaryContainer = MuniColors.PrimaryFixed,
    onPrimaryContainer = MuniColors.OnPrimaryFixed,
    secondary = MuniColors.Secondary,
    onSecondary = MuniColors.White,
    secondaryContainer = MuniColors.SecondaryFixed,
    onSecondaryContainer = MuniColors.OnSecondaryContainer,
    tertiary = MuniColors.Tertiary,
    onTertiary = MuniColors.White,
    tertiaryContainer = MuniColors.TertiaryFixed,
    onTertiaryContainer = Color(0xFF00210A),
    error = MuniColors.Error,
    onError = MuniColors.White,
    errorContainer = MuniColors.ErrorContainer,
    onErrorContainer = Color(0xFF93000A),
    background = MuniColors.Background,
    onBackground = MuniColors.OnSurface,
    surface = MuniColors.Surface,
    onSurface = MuniColors.OnSurface,
    surfaceVariant = MuniColors.SurfaceContainer,
    onSurfaceVariant = MuniColors.OnSurfaceVariant,
    outline = MuniColors.Outline,
    outlineVariant = MuniColors.OutlineVariant,
    inverseSurface = Color(0xFF263143),
    inverseOnSurface = Color(0xFFECF1FF),
    inversePrimary = MuniColors.PrimaryFixedDim,
    scrim = Color.Black,
)

// Las referencias Stitch definen un único universo visual claro.
internal val MuniDarkColorScheme = MuniLightColorScheme
