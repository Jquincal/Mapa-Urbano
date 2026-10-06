package com.mapaurbano.app.feature.report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.Category
import com.mapaurbano.app.core.model.ReportDraft
import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.feature.common.MiniMapIllustration
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchPrimaryButton

@Composable
fun ReportFormScreen(
    draft: ReportDraft,
    categories: List<Category>,
    isSignedIn: Boolean,
    modifier: Modifier = Modifier,
    validationMessage: String? = null,
    onDraftChange: (ReportDraft) -> Unit,
    onSubmissionModeChange: (SubmissionMode) -> Unit,
    onChooseLocation: () -> Unit,
    onPhotoRequest: () -> Unit,
    onRemovePhoto: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    isSubmitting: Boolean = false,
) {
    val titleError = validationMessage != null && draft.title.trim().length < 5
    val categoryError = validationMessage != null && draft.categoryId == null
    val locationError = validationMessage != null && draft.location.isBlank()
    val controlsEnabled = !isSubmitting
    val scrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }
    val titleFocusRequester = remember { FocusRequester() }

    LaunchedEffect(validationMessage, titleError, categoryError, locationError) {
        if (validationMessage == null) return@LaunchedEffect
        when {
            titleError -> titleFocusRequester.requestFocus()
            categoryError -> scrollState.animateScrollTo(CATEGORY_SCROLL_OFFSET)
            locationError -> scrollState.animateScrollTo(LOCATION_SCROLL_OFFSET)
        }
    }

    ScrollableFeatureScreen(
        title = "Nuevo reporte",
        modifier = modifier,
        onBack = onBack,
        scrollState = scrollState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("PASO A PASO", style = MaterialTheme.typography.labelSmall, color = MuniColors.Primary, fontWeight = FontWeight.Bold)
                Text("Paso 1 de 2", style = MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
            }
            Text("Nuevo reporte vecinal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MuniColors.SurfaceContainer, RoundedCornerShape(50)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(.66f)
                        .height(6.dp)
                        .background(MuniColors.Primary, RoundedCornerShape(50)),
                )
            }
        }

        ModeSelector(
            selected = draft.submissionMode,
            accountAvailable = isSignedIn,
            enabled = controlsEnabled,
            onSubmissionModeChange = onSubmissionModeChange,
        )

        FieldLabel("Título breve", required = true)
        OutlinedTextField(
            value = draft.title,
            onValueChange = { onDraftChange(draft.copy(title = it.take(TITLE_MAX_LENGTH))) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(titleFocusRequester),
            placeholder = { Text("Ej: Baches profundos frente al cruce peatonal") },
            leadingIcon = { Icon(Icons.Rounded.EditNote, contentDescription = null) },
            supportingText = {
                Text(if (titleError) "Ingresá al menos 5 caracteres." else "${draft.title.length}/$TITLE_MAX_LENGTH")
            },
            isError = titleError,
            enabled = controlsEnabled,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next,
            ),
        )

        FieldLabel("Categoría del problema", required = true)
        CategoryGrid(
            categories = categories,
            selectedCategoryId = draft.categoryId,
            enabled = controlsEnabled,
            hasError = categoryError,
            onSelected = { onDraftChange(draft.copy(categoryId = it)) },
        )
        if (categoryError) {
            Text("Seleccioná una categoría.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        FieldLabel("Descripción detallada")
        OutlinedTextField(
            value = draft.description,
            onValueChange = { onDraftChange(draft.copy(description = it.take(DESCRIPTION_MAX_LENGTH))) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Detalla referencias, horario o condiciones especificas...") },
            supportingText = { Text("${draft.description.length}/$DESCRIPTION_MAX_LENGTH") },
            enabled = controlsEnabled,
            minLines = 3,
            maxLines = 6,
            shape = RoundedCornerShape(14.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default,
            ),
        )

        LocationCard(
            draft = draft,
            hasError = locationError,
            enabled = controlsEnabled,
            onChooseLocation = onChooseLocation,
        )

        PhotoCard(
            hasPhoto = draft.hasPhoto,
            enabled = controlsEnabled,
            onPhotoRequest = onPhotoRequest,
            onRemovePhoto = onRemovePhoto,
        )
        draft.photoIssue?.let { issue ->
            Text(
                text = issue.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.semantics { error(issue.message) },
            )
        }

        if (validationMessage != null) {
            Text(
                text = validationMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { error(validationMessage) },
            )
        }

        StitchPrimaryButton(
            text = if (isSubmitting) "Enviando..." else "ENVIAR REPORTE",
            onClick = onSubmit,
            enabled = !isSubmitting,
            icon = if (isSubmitting) null else Icons.Rounded.ArrowForward,
        )
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally).size(22.dp),
                strokeWidth = 2.dp,
                color = MuniColors.Primary,
            )
        }
    }
}

@Composable
private fun ModeSelector(
    selected: SubmissionMode,
    accountAvailable: Boolean,
    enabled: Boolean,
    onSubmissionModeChange: (SubmissionMode) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Modalidad del reporte")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            ModeCard(
                mode = SubmissionMode.Account,
                selected = selected == SubmissionMode.Account,
                enabled = enabled,
                icon = Icons.Rounded.AccountCircle,
                title = "Guardar en mi cuenta",
                description = if (accountAvailable) "Vinculado a tu cuenta." else "Te pediremos iniciar sesión.",
                modifier = Modifier.weight(1f),
                onClick = { onSubmissionModeChange(SubmissionMode.Account) },
            )
            ModeCard(
                mode = SubmissionMode.Anonymous,
                selected = selected == SubmissionMode.Anonymous,
                enabled = enabled,
                icon = Icons.Rounded.Security,
                title = "Anonimo",
                description = "Recibirás un código seguro.",
                modifier = Modifier.weight(1f),
                onClick = { onSubmissionModeChange(SubmissionMode.Anonymous) },
            )
        }
    }
}

@Composable
private fun ModeCard(
    mode: SubmissionMode,
    selected: Boolean,
    enabled: Boolean,
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    StitchCard(
        modifier = modifier
            .alpha(if (enabled) 1f else DISABLED_CONTROL_ALPHA)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MuniColors.Primary else MuniColors.OutlineVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick),
        containerColor = MuniColors.SurfaceContainerLowest,
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RadioButton(selected = selected, onClick = null, enabled = enabled)
            Icon(icon, contentDescription = null, tint = if (selected) MuniColors.Primary else MuniColors.Outline)
        }
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(description, style = MaterialTheme.typography.bodySmall, color = MuniColors.OnSurfaceVariant)
    }
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    selectedCategoryId: String?,
    enabled: Boolean,
    hasError: Boolean,
    onSelected: (String) -> Unit,
) {
    Column(
        modifier = if (hasError) Modifier.semantics { error("Seleccioná una categoría.") } else Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { category ->
                    CategoryOption(
                        category = category,
                        selected = selectedCategoryId == category.id,
                        enabled = enabled,
                        onClick = { onSelected(category.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CategoryOption(
    category: Category,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else DISABLED_CONTROL_ALPHA)
            .height(58.dp)
            .background(
                if (selected) MuniColors.PrimaryFixed else MuniColors.SurfaceContainerLowest,
                RoundedCornerShape(14.dp),
            )
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) MuniColors.Primary else MuniColors.OutlineVariant,
                RoundedCornerShape(14.dp),
            )
            .clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Rounded.Assignment, contentDescription = null, tint = MuniColors.Orange700, modifier = Modifier.size(20.dp))
        Text(
            text = category.label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun LocationCard(
    draft: ReportDraft,
    hasError: Boolean,
    enabled: Boolean,
    onChooseLocation: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Ubicacion del incidente", required = true, trailing = "Precision GPS alta")
        StitchCard(
            modifier = Modifier.semantics {
                if (hasError) error("Elegí una ubicación.")
            },
            containerColor = if (hasError) MaterialTheme.colorScheme.errorContainer else MuniColors.SurfaceContainerLowest,
            contentPadding = PaddingValues(12.dp),
        ) {
            MiniMapIllustration(modifier = Modifier.height(120.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = MuniColors.Primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(draft.location.ifBlank { "Sin ubicación" }, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                Text("Confirmado", color = MuniColors.Green700, style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = onChooseLocation,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 44.dp),
                shape = RoundedCornerShape(50),
            ) {
                Icon(Icons.Rounded.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Ajustar mapa")
            }
        }
    }
}

@Composable
private fun PhotoCard(
    hasPhoto: Boolean,
    enabled: Boolean,
    onPhotoRequest: () -> Unit,
    onRemovePhoto: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel("Fotografia de evidencia")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuniColors.SurfaceContainerLowest, RoundedCornerShape(16.dp))
                .border(1.dp, MuniColors.OutlineVariant, RoundedCornerShape(16.dp))
                .clickable(enabled = enabled, onClick = onPhotoRequest)
                .padding(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MuniColors.PrimaryFixed, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (hasPhoto) Icons.Rounded.CheckCircle else Icons.Rounded.AddAPhoto,
                        contentDescription = null,
                        tint = MuniColors.Primary,
                    )
                }
                Text(
                    text = if (hasPhoto) "Foto de referencia adjunta" else "Tomar foto o subir archivo",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (hasPhoto) "Toca para cambiar o usa quitar." else "JPG, PNG o WebP (max. 5 MB)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MuniColors.OnSurfaceVariant,
                )
                if (hasPhoto) {
                    OutlinedButton(onClick = onRemovePhoto, enabled = enabled, shape = RoundedCornerShape(50)) {
                        Text("Quitar foto")
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(title: String, required: Boolean = false, trailing: String? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        when {
            required -> Text("* Requerido", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            trailing != null -> Text(trailing, style = MaterialTheme.typography.labelSmall, color = MuniColors.Green700)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MuniColors.SurfaceContainerLowest,
    unfocusedContainerColor = MuniColors.SurfaceContainerLowest,
    focusedBorderColor = MuniColors.OutlineVariant,
    unfocusedBorderColor = MuniColors.OutlineVariant,
)

private const val TITLE_MAX_LENGTH = 80
private const val DESCRIPTION_MAX_LENGTH = 500
private const val CATEGORY_SCROLL_OFFSET = 360
private const val LOCATION_SCROLL_OFFSET = 940
private const val DISABLED_CONTROL_ALPHA = 0.6f
