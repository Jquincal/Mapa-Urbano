package com.mapaurbano.app.feature.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.designsystem.MuniDimensions
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ReportStatus

@Composable
fun StitchTopBar(
    subtitle: String,
    modifier: Modifier = Modifier,
    title: String = "MuniReport",
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = { DefaultTopBarActions() },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(MuniDimensions.TopBarHeight)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp),
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                }
            } else {
                MuniLogo()
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            actions()
        }
    }
}

@Composable
fun DefaultTopBarActions(onProfile: (() -> Unit)? = null, onNotifications: (() -> Unit)? = null) {
    IconButton(onClick = { onNotifications?.invoke() }, modifier = Modifier.size(44.dp)) {
        Icon(
            Icons.Rounded.NotificationsNone,
            contentDescription = "Notificaciones",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    IconButton(onClick = { onProfile?.invoke() }, modifier = Modifier.size(44.dp)) {
        Icon(
            Icons.Rounded.AccountCircle,
            contentDescription = "Perfil de usuario",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun MuniLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun StitchCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MuniColors.SurfaceContainerLowest,
    contentPadding: PaddingValues = PaddingValues(14.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

@Composable
fun StitchSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Rounded.Search,
    trailing: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        textStyle = MaterialTheme.typography.bodyMedium,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        placeholder = { Text(placeholder, maxLines = 1) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = trailing,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MuniColors.SurfaceContainerLowest,
            unfocusedContainerColor = MuniColors.SurfaceContainerLowest,
            focusedBorderColor = MuniColors.OutlineVariant,
            unfocusedBorderColor = MuniColors.OutlineVariant,
            focusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
        ),
    )
}

@Composable
fun StitchChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    val background = if (selected) selectedColor else Color.Transparent
    val content = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = background,
        contentColor = content,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MuniColors.OutlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (icon != null) Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
fun StitchSegmented(
    first: String,
    second: String,
    firstSelected: Boolean,
    onFirst: () -> Unit,
    onSecond: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MuniColors.SurfaceContainer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        StitchSegmentItem(first, firstSelected, onFirst, Modifier.weight(1f))
        StitchSegmentItem(second, !firstSelected, onSecond, Modifier.weight(1f))
    }
}

@Composable
private fun StitchSegmentItem(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (selected) MuniColors.SurfaceContainerLowest else Color.Transparent,
        contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = if (selected) 1.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun StitchStatusPill(status: ReportStatus, modifier: Modifier = Modifier) {
    val (background, foreground) = when (status) {
        ReportStatus.Pending -> MuniColors.Orange to Color(0xFF2F1500)
        ReportStatus.InProgress -> MuniColors.PrimaryBright to Color.White
        ReportStatus.Resolved -> MuniColors.Green100 to Color(0xFF00210A)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = background,
        contentColor = foreground,
    ) {
        Text(
            text = status.label.uppercase(),
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
    }
}

@Composable
fun StitchReportThumbnail(
    report: Report,
    modifier: Modifier = Modifier,
    size: Dp = 76.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(MuniColors.SurfaceContainerHigh),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(MuniColors.SurfaceContainer)
            drawLine(Color.White, Offset(0f, this.size.height * .38f), Offset(this.size.width, this.size.height * .38f), 9.dp.toPx(), StrokeCap.Round)
            drawLine(Color.White, Offset(this.size.width * .38f, 0f), Offset(this.size.width * .38f, this.size.height), 9.dp.toPx(), StrokeCap.Round)
            drawCircle(
                color = when (report.status) {
                    ReportStatus.Pending -> MuniColors.Orange
                    ReportStatus.InProgress -> MuniColors.PrimaryBright
                    ReportStatus.Resolved -> MuniColors.Green700
                },
                radius = 9.dp.toPx(),
                center = Offset(this.size.width * report.point.x.coerceIn(.18f, .82f), this.size.height * report.point.y.coerceIn(.18f, .82f)),
            )
        }
        Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp),
        )
    }
}

@Composable
fun PhotoThumb(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 76.dp, height = 72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MuniColors.SurfaceContainerHigh),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(MuniColors.PrimaryFixed)
            drawCircle(MuniColors.Orange.copy(alpha = .35f), 28.dp.toPx(), Offset(size.width * .76f, size.height * .20f))
            drawLine(
                color = MuniColors.Green100,
                start = Offset(0f, size.height * .82f),
                end = Offset(size.width, size.height * .52f),
                strokeWidth = 44.dp.toPx(),
            )
        }
    }
}

@Composable
fun StitchReportCard(
    report: Report,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = false,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClickLabel = "Ver detalle", onClick = onClick)
            .semantics {
                contentDescription = "${report.title}. ${report.status.label}. ${report.location}"
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MuniColors.SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            if (report.hasPhoto) {
                PhotoThumb()
            } else {
                StitchReportThumbnail(report = report)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            report.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            report.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    StitchStatusPill(report.status)
                }
                Text(
                    text = report.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = report.createdAtLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (showActions) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MiniAction("Ver detalle", selected = true)
                        MiniAction("Actividad", selected = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniAction(label: String, selected: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) MaterialTheme.colorScheme.primary else MuniColors.SurfaceContainer,
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun StitchMiniMap(
    modifier: Modifier = Modifier,
    reports: List<Report> = emptyList(),
    selectedReportId: String? = null,
    onReportClick: ((Report) -> Unit)? = null,
    showUserLocation: Boolean = true,
    onLocate: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MuniColors.SurfaceContainer),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(MuniColors.SurfaceContainer)
            val block = MuniColors.SurfaceContainerLow
            listOf(
                Pair(Offset(size.width * .05f, size.height * .07f), Offset(size.width * .28f, size.height * .22f)),
                Pair(Offset(size.width * .34f, size.height * .07f), Offset(size.width * .70f, size.height * .22f)),
                Pair(Offset(size.width * .75f, size.height * .07f), Offset(size.width * .95f, size.height * .22f)),
                Pair(Offset(size.width * .05f, size.height * .30f), Offset(size.width * .28f, size.height * .47f)),
                Pair(Offset(size.width * .34f, size.height * .30f), Offset(size.width * .70f, size.height * .47f)),
                Pair(Offset(size.width * .75f, size.height * .30f), Offset(size.width * .95f, size.height * .47f)),
                Pair(Offset(size.width * .05f, size.height * .55f), Offset(size.width * .47f, size.height * .75f)),
                Pair(Offset(size.width * .53f, size.height * .55f), Offset(size.width * .95f, size.height * .75f)),
            ).forEach { (topLeft, bottomRight) ->
                drawRoundRect(
                    color = block,
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(bottomRight.x - topLeft.x, bottomRight.y - topLeft.y),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                )
            }
            drawRoundRect(
                color = MuniColors.Green100.copy(alpha = .45f),
                topLeft = Offset(size.width * .34f, size.height * .28f),
                size = androidx.compose.ui.geometry.Size(size.width * .36f, size.height * .20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
            )
            listOf(.26f, .50f, .78f).forEach { y ->
                drawLine(Color.White, Offset(0f, size.height * y), Offset(size.width, size.height * y), 16.dp.toPx(), StrokeCap.Round)
            }
            listOf(.31f, .72f).forEach { x ->
                drawLine(Color.White, Offset(size.width * x, 0f), Offset(size.width * x, size.height), 16.dp.toPx(), StrokeCap.Round)
            }
            drawLine(
                Color.White,
                Offset(-20f, size.height * .68f),
                Offset(size.width + 20f, size.height * .38f),
                26.dp.toPx(),
                StrokeCap.Round,
            )
            drawLine(
                MuniColors.PrimaryFixedDim,
                Offset(-20f, size.height * .68f),
                Offset(size.width + 20f, size.height * .38f),
                2.dp.toPx(),
                StrokeCap.Round,
            )
            if (showUserLocation) {
                drawCircle(MuniColors.PrimaryBright.copy(alpha = .15f), 24.dp.toPx(), Offset(size.width * .20f, size.height * .39f))
                drawCircle(MuniColors.PrimaryBright.copy(alpha = .25f), 14.dp.toPx(), Offset(size.width * .20f, size.height * .39f))
                drawCircle(MuniColors.PrimaryBright, 7.dp.toPx(), Offset(size.width * .20f, size.height * .39f))
                drawCircle(Color.White, 9.dp.toPx(), Offset(size.width * .20f, size.height * .39f), style = Stroke(2.5.dp.toPx()))
            }
        }
        reports.forEachIndexed { index, report ->
            val isSelected = report.id == selectedReportId
            val color = when (report.status) {
                ReportStatus.Pending -> MuniColors.Orange
                ReportStatus.InProgress -> MuniColors.PrimaryBright
                ReportStatus.Resolved -> Color(0xFF111C2D)
            }
            MapMarker(
                color = color,
                selected = isSelected,
                index = index,
                report = report,
                onClick = onReportClick,
            )
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = .92f),
        ) {
            Text(
                text = "AV. SAN MARTIN",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = onLocate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(MuniColors.SurfaceContainerLowest),
        ) {
            Icon(Icons.Rounded.MyLocation, contentDescription = "Centrar mapa", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun BoxScope.MapMarker(
    color: Color,
    selected: Boolean,
    index: Int,
    report: Report,
    onClick: ((Report) -> Unit)?,
) {
    val alignments = listOf(
        Alignment.CenterEnd,
        Alignment.TopCenter,
        Alignment.BottomStart,
        Alignment.Center,
        Alignment.BottomEnd,
    )
    IconButton(
        onClick = { onClick?.invoke(report) },
        enabled = onClick != null,
        modifier = Modifier
            .align(alignments[index % alignments.size])
            .padding(
                start = 26.dp + (index % 2 * 22).dp,
                top = 46.dp + (index % 3 * 18).dp,
                end = 36.dp + (index % 2 * 18).dp,
                bottom = 58.dp + (index % 3 * 16).dp,
            )
            .size(if (selected) 50.dp else 44.dp)
            .clip(CircleShape)
            .background(color)
            .border(if (selected) 4.dp else 2.dp, Color.White, CircleShape)
            .semantics { contentDescription = "Marcador ${index + 1}: ${report.title}" },
    ) {
        Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White)
    }
}

@Composable
fun StitchBottomNavigation(
    selectedLabel: String,
    onMap: () -> Unit,
    onCreate: () -> Unit,
    onMyReports: () -> Unit,
    onTracking: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MuniColors.SurfaceContainerLowest,
        shadowElevation = 4.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    ),
            ) {
        val items = listOf(
            Triple("Mapa", Icons.Rounded.Map, onMap),
            Triple("Reportar", Icons.Rounded.AddLocationAlt, onCreate),
                    Triple("Mis reportes", Icons.AutoMirrored.Rounded.Assignment, onMyReports),
            Triple("Seguimiento", Icons.Rounded.Radar, onTracking),
        )
        items.forEach { (label, icon, action) ->
            val selected = selectedLabel == label
                    val accent = when {
                        label == "Reportar" -> MuniColors.OrangeDark
                        selected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(role = Role.Tab, onClick = action)
                            .semantics { this.selected = selected }
                            .padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 52.dp, height = 30.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    when {
                                        !selected -> Color.Transparent
                                        label == "Reportar" -> MuniColors.Orange.copy(alpha = .25f)
                                        else -> MuniColors.SurfaceContainerHigh
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(23.dp),
                                tint = accent,
                            )
                        }
                        Text(
                            text = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 1.dp, start = 1.dp, end = 1.dp),
                            color = accent,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsBottomHeight(WindowInsets.navigationBars),
            )
        }
    }
}

@Composable
fun StitchSuccessIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(MuniColors.Green100),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MuniColors.Green700, modifier = Modifier.size(44.dp))
    }
}

@Composable
fun StitchPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    text: String? = null,
    content: @Composable (() -> Unit)? = null,
) {
    val resolvedLabel = label ?: text.orEmpty()
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 50.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = MuniColors.PrimaryBright),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    ) {
        if (content != null) {
            content()
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(resolvedLabel, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun MiniMapIllustration(
    modifier: Modifier = Modifier,
    label: String = "Mapa simulado",
    showPin: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MuniColors.SurfaceContainer),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(MuniColors.SurfaceContainer)
            drawRoundRect(
                color = MuniColors.SurfaceContainerLow,
                topLeft = Offset(size.width * .08f, size.height * .12f),
                size = androidx.compose.ui.geometry.Size(size.width * .32f, size.height * .28f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
            )
            drawRoundRect(
                color = MuniColors.Green100.copy(alpha = .4f),
                topLeft = Offset(size.width * .46f, size.height * .12f),
                size = androidx.compose.ui.geometry.Size(size.width * .38f, size.height * .30f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx()),
            )
            drawRoundRect(
                color = MuniColors.SurfaceContainerLow,
                topLeft = Offset(size.width * .08f, size.height * .58f),
                size = androidx.compose.ui.geometry.Size(size.width * .78f, size.height * .25f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
            )
            drawLine(Color.White, Offset(0f, size.height * .50f), Offset(size.width, size.height * .50f), 13.dp.toPx(), StrokeCap.Round)
            drawLine(Color.White, Offset(size.width * .43f, 0f), Offset(size.width * .43f, size.height), 13.dp.toPx(), StrokeCap.Round)
            drawLine(Color.White, Offset(-10f, size.height * .82f), Offset(size.width + 10f, size.height * .30f), 18.dp.toPx(), StrokeCap.Round)
            if (showPin) {
                drawCircle(MuniColors.PrimaryBright.copy(alpha = .15f), 20.dp.toPx(), Offset(size.width * .58f, size.height * .44f))
                drawCircle(MuniColors.PrimaryBright, 8.dp.toPx(), Offset(size.width * .58f, size.height * .44f))
                drawCircle(Color.White, 10.dp.toPx(), Offset(size.width * .58f, size.height * .44f), style = Stroke(2.dp.toPx()))
            }
        }
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = .92f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun CompositionLocalBrandContent(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides color, content = content)
}
