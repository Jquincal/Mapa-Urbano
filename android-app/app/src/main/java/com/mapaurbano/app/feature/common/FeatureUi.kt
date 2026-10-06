package com.mapaurbano.app.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import com.mapaurbano.app.core.designsystem.MuniColors

/** Estado visual común. El origen de datos real puede adaptarse a este contrato más adelante. */
sealed interface ContentState {
    data object Ready : ContentState
    data object Loading : ContentState
    data class Empty(val message: String) : ContentState
    data class Error(val message: String) : ContentState
}

@Composable
fun FeatureTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    StitchTopBar(
        modifier = modifier,
        title = if (onBack == null) "MuniReport" else title,
        subtitle = if (onBack == null) title else "MuniReport",
        onBack = onBack,
        actions = if (onBack == null) actions else ({}),
    )
}

/**
 * Base para contenido corto y formularios. Es el único propietario del scroll vertical,
 * y deja que IME/insets reduzcan el área útil sin tapar la última acción.
 */
@Composable
fun ScrollableFeatureScreen(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    scrollState: ScrollState? = null,
    topBarActions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val resolvedScrollState = scrollState ?: rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(initial = 0)
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MuniColors.Surface,
        topBar = { FeatureTopBar(title = title, onBack = onBack, actions = topBarActions) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(resolvedScrollState)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content,
        )
    }
}

@Composable
fun ContentStatePanel(
    state: ContentState,
    modifier: Modifier = Modifier,
    emptyActionLabel: String? = null,
    onEmptyAction: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
) {
    when (state) {
        ContentState.Ready -> Unit
        ContentState.Loading -> StatePanel(
            modifier = modifier,
            title = "Cargando",
            message = "Estamos preparando la información.",
            progress = true,
        )

        is ContentState.Empty -> StatePanel(
            modifier = modifier,
            icon = Icons.Rounded.Inbox,
            title = "Todavía no hay resultados",
            message = state.message,
            actionLabel = emptyActionLabel,
            onAction = onEmptyAction,
        )

        is ContentState.Error -> StatePanel(
            modifier = modifier,
            icon = Icons.Rounded.ErrorOutline,
            title = "No pudimos cargar la información",
            message = state.message,
            actionLabel = if (onRetry != null) "Reintentar" else null,
            onAction = onRetry,
        )
    }
}

@Composable
private fun StatePanel(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    progress: Boolean = false,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MuniColors.SurfaceContainerLowest),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when {
                    progress -> CircularProgressIndicator(
                        modifier = Modifier.semantics { },
                    )
                    icon != null -> Icon(icon, contentDescription = null)
                }
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(message, style = MaterialTheme.typography.bodyMedium)
                if (actionLabel != null && onAction != null) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier.sizeIn(minHeight = 48.dp),
                    ) { Text(actionLabel) }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, supportingText: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
        if (supportingText != null) {
            Text(supportingText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NoticeCard(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MuniColors.Orange100, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(text, color = MuniColors.Orange700)
    }
}
