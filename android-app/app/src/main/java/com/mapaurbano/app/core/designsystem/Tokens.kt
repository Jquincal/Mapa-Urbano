package com.mapaurbano.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/** Spacing tokens based on a 4 dp grid. */
object MuniSpacing {
    val None = 0.dp
    val Xxs = 4.dp
    val Xs = 8.dp
    val Sm = 12.dp
    val Md = 16.dp
    val Lg = 24.dp
    val Xl = 32.dp
    val Xxl = 48.dp
}

object MuniDimensions {
    /** Minimum interactive size recommended by Android accessibility guidance. */
    val TouchTarget = 48.dp
    val IconSmall = 18.dp
    val Icon = 24.dp
    val IconLarge = 40.dp
    val ContentMaxWidth = 560.dp
    val CardElevation = 2.dp
    val BottomNavHeight = 80.dp
    val TopBarHeight = 64.dp
}

object MuniShapes {
    val Small = RoundedCornerShape(12.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(20.dp)
    val Pill = RoundedCornerShape(50)
}
