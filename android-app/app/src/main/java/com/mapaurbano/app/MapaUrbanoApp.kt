package com.mapaurbano.app

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapaurbano.app.core.data.AppDialog
import com.mapaurbano.app.core.data.AppUiState
import com.mapaurbano.app.core.data.AppViewModel
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ContentLoadState
import com.mapaurbano.app.core.model.PhotoIssue
import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.core.navigation.AppDestination
import com.mapaurbano.app.core.navigation.isMainDestination
import com.mapaurbano.app.feature.auth.LoginScreen
import com.mapaurbano.app.feature.auth.ProfileScreen
import com.mapaurbano.app.feature.auth.RegisterScreen
import com.mapaurbano.app.feature.common.ContentState
import com.mapaurbano.app.feature.common.NoticeCard
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.StitchBottomNavigation
import com.mapaurbano.app.feature.map.MapScreen
import com.mapaurbano.app.feature.myreports.MyReportsScreen
import com.mapaurbano.app.feature.myreports.ReportDetailScreen
import com.mapaurbano.app.feature.report.ConfirmationScreen
import com.mapaurbano.app.feature.report.LocationPickerScreen
import com.mapaurbano.app.feature.report.ReportFormScreen
import com.mapaurbano.app.feature.tracking.TrackingScreen

@Composable
fun MapaUrbanoApp(appViewModel: AppViewModel = viewModel()) {
    val state by appViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var email by rememberSaveable { mutableStateOf("valeria@correo.com") }
    var password by remember { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(state.user) {
        if (state.user != null) {
            password = ""
            confirmPassword = ""
        }
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            appViewModel.consumeMessage()
        }
    }

    BackHandler(enabled = state.destination != AppDestination.Map || state.dialog != null) {
        if (state.destination is AppDestination.Login || state.destination is AppDestination.Register) {
            password = ""
            confirmPassword = ""
        }
        appViewModel.back()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (state.destination.isMainDestination) {
                MainNavigationBar(
                    destination = state.destination,
                    onNavigate = { appViewModel.navigate(it) },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            DestinationContent(
                state = state,
                viewModel = appViewModel,
                email = email,
                password = password,
                displayName = displayName,
                confirmPassword = confirmPassword,
                onEmailChange = {
                    email = it
                    appViewModel.clearAuthError()
                },
                onPasswordChange = {
                    password = it
                    appViewModel.clearAuthError()
                },
                onDisplayNameChange = {
                    displayName = it
                    appViewModel.clearAuthError()
                },
                onConfirmPasswordChange = {
                    confirmPassword = it
                    appViewModel.clearAuthError()
                },
                onClearSecrets = {
                    password = ""
                    confirmPassword = ""
                },
                onCopyCode = { code ->
                    runCatching { clipboard.setText(AnnotatedString(code)) }
                        .onSuccess { appViewModel.notify("Código copiado.") }
                        .onFailure { appViewModel.notify("No pudimos copiar el código. Mantenelo presionado para seleccionarlo.") }
                },
                onPasteCode = {
                    val pasted = clipboard.getText()?.text?.trim().orEmpty()
                    if (pasted.isBlank()) {
                        appViewModel.notify("No hay un código disponible en el portapapeles.")
                    } else {
                        appViewModel.setTrackingQuery(pasted)
                    }
                },
                onShareText = { text ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Compartir con"))
                },
            )

        }
    }

    AppDialogHost(
        dialog = state.dialog,
        onDismiss = appViewModel::dismissDialog,
        onKeepDraft = appViewModel::keepDraftAndContinue,
        onDiscardDraft = appViewModel::discardDraftAndContinue,
        onDeactivate = appViewModel::deactivateAccount,
        onAttachPhoto = { appViewModel.setPhoto(true) },
        onPhotoInvalidType = { appViewModel.setPhoto(false, PhotoIssue.InvalidType) },
        onPhotoTooLarge = { appViewModel.setPhoto(false, PhotoIssue.TooLarge) },
        onPhotoPermissionDenied = { appViewModel.setPhoto(false, PhotoIssue.PermissionDenied) },
        onSimulateContentError = appViewModel::simulateContentError,
        onFailNextSubmission = appViewModel::failNextSubmission,
        onSimulateSessionExpiry = appViewModel::simulateSessionExpiry,
    )
}

@Composable
private fun DestinationContent(
    state: AppUiState,
    viewModel: AppViewModel,
    email: String,
    password: String,
    displayName: String,
    confirmPassword: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onClearSecrets: () -> Unit,
    onCopyCode: (String) -> Unit,
    onPasteCode: () -> Unit,
    onShareText: (String) -> Unit,
) {
    when (val destination = state.destination) {
        AppDestination.Map -> MapScreen(
            reports = state.visibleReports,
            categories = state.categories,
            filters = state.mapFilters,
            presentation = state.mapPresentation,
            contentLoadState = state.contentLoadState,
            onQueryChange = viewModel::setQuery,
            onPresentationChange = viewModel::setMapPresentation,
            onStatusFilterChange = viewModel::setStatusFilter,
            onCategoryFilterChange = viewModel::setCategoryFilter,
            onClearFilters = viewModel::clearFilters,
            onReportClick = { viewModel.navigate(AppDestination.ReportDetail(it.id)) },
            onReportSelect = viewModel::selectReportOnMap,
            onCreateReport = { viewModel.navigate(AppDestination.CreateReport) },
            onRetry = viewModel::retryContent,
            selectedReportId = state.selectedReportId,
            onProfile = { viewModel.navigate(AppDestination.Profile) },
            onNotifications = {
                viewModel.showDialog(AppDialog.FutureFeature("Notificaciones"))
            },
            onLocate = {
                viewModel.notify("Ubicación centrada en Plaza Central.")
            },
        )

        AppDestination.CreateReport -> ReportFormScreen(
            draft = state.draft,
            categories = state.categories,
            isSignedIn = state.user != null,
            validationMessage = state.formError,
            onDraftChange = { draft -> viewModel.updateDraft { draft } },
            onSubmissionModeChange = viewModel::setSubmissionMode,
            onChooseLocation = { viewModel.navigate(AppDestination.LocationPicker) },
            onPhotoRequest = { viewModel.showDialog(AppDialog.PhotoPicker) },
            onRemovePhoto = { viewModel.setPhoto(false) },
            onSubmit = viewModel::submitReport,
            onBack = viewModel::back,
            isSubmitting = state.isSubmitting,
        )

        AppDestination.MyReports -> MyReportsScreen(
            reports = state.ownedReports,
            categories = state.categories,
            filters = state.myReportsFilters,
            contentState = when (state.myReportsLoadState) {
                ContentLoadState.Ready -> ContentState.Ready
                ContentLoadState.Loading -> ContentState.Loading
                ContentLoadState.Error -> ContentState.Error("Revisá tu conexión e intentá nuevamente.")
            },
            onQueryChange = viewModel::setMyReportsQuery,
            onStatusFilterChange = viewModel::setMyReportsStatus,
            onCategoryFilterChange = viewModel::setMyReportsCategory,
            onClearFilters = viewModel::clearMyReportsFilters,
            onReportClick = { viewModel.navigate(AppDestination.ReportDetail(it.id)) },
            onCreateReport = { viewModel.navigate(AppDestination.CreateReport) },
            onRetry = viewModel::retryContent,
            onProfile = { viewModel.navigate(AppDestination.Profile) },
            onNotifications = {
                viewModel.showDialog(AppDialog.FutureFeature("Notificaciones"))
            },
        )

        AppDestination.Tracking -> TrackingScreen(
            query = state.trackingQuery,
            report = state.report(state.trackedReportId),
            errorMessage = state.trackingError,
            onQueryChange = viewModel::setTrackingQuery,
            onTrack = viewModel::trackReport,
            onReportClick = { viewModel.navigate(AppDestination.ReportDetail(it.id)) },
            isLoading = state.isTracking,
            onPaste = onPasteCode,
            onProfile = { viewModel.navigate(AppDestination.Profile) },
            onNotifications = {
                viewModel.showDialog(AppDialog.FutureFeature("Notificaciones"))
            },
        )

        AppDestination.Profile -> state.user?.let { user ->
            ProfileScreen(
                user = user,
                message = state.message,
                onBack = viewModel::back,
                onEditProfile = { viewModel.showDialog(AppDialog.FutureFeature("Editar perfil")) },
                onChangePassword = { viewModel.showDialog(AppDialog.FutureFeature("Cambiar contraseña")) },
                onViewReports = { viewModel.navigate(AppDestination.MyReports) },
                onLogout = viewModel::logout,
                onDeactivateAccount = { viewModel.showDialog(AppDialog.DeactivateAccount) },
            )
        }

        is AppDestination.Login -> LoginScreen(
            email = email,
            password = password,
            message = state.authError,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onLogin = { viewModel.login(email, password) },
            onRegister = {
                onClearSecrets()
                viewModel.navigate(AppDestination.Register(destination.returnTo))
            },
            onBack = {
                onClearSecrets()
                viewModel.back()
            },
            onForgotPassword = { viewModel.showDialog(AppDialog.FutureFeature("Recuperar contraseña")) },
            onUnavailableFeature = { title ->
                viewModel.showDialog(AppDialog.FutureFeature(title))
            },
        )

        is AppDestination.Register -> RegisterScreen(
            displayName = displayName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            message = state.authError,
            onDisplayNameChange = onDisplayNameChange,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onRegister = {
                if (password == confirmPassword) {
                    viewModel.register(displayName, email, password)
                } else {
                    viewModel.notify("Las contraseñas no coinciden.")
                }
            },
            onBack = {
                onClearSecrets()
                viewModel.back()
            },
        )

        is AppDestination.ReportDetail -> {
            val report = state.report(destination.reportId)
            if (report == null) {
                UnavailableReportScreen(onBack = viewModel::back)
            } else {
                ReportDetailScreen(
                    report = report,
                    showPrivateInformation = report.ownerId != null && state.user?.id == report.ownerId,
                    onBack = viewModel::back,
                    onViewOnMap = viewModel::focusReportOnMap,
                    onShare = { shareReport ->
                        onShareText("${shareReport.title} · ${shareReport.location} · ${shareReport.status.label}")
                    },
                )
            }
        }

        is AppDestination.Confirmation -> {
            val report = state.report(destination.reportId)
            if (report == null) {
                UnavailableReportScreen(onBack = viewModel::back)
            } else {
                ConfirmationScreen(
                    report = report,
                    onCopyTrackingCode = onCopyCode,
                    onShareTrackingCode = { onShareText("Código MuniReport: $it") },
                    onOpenReport = {
                        if (report.submissionMode == SubmissionMode.Anonymous) {
                            viewModel.openTrackingFor(report)
                        } else {
                            viewModel.navigate(AppDestination.ReportDetail(report.id))
                        }
                    },
                    onCreateAnother = { viewModel.navigate(AppDestination.CreateReport) },
                    onGoToMap = { viewModel.navigate(AppDestination.Map, replace = true) },
                )
            }
        }

        AppDestination.LocationPicker -> LocationPickerScreen(
            initialPoint = state.draft.point,
            initialLocation = state.draft.location,
            onConfirm = viewModel::confirmLocation,
            onCancel = viewModel::back,
        )
    }
}

@Composable
private fun MainNavigationBar(
    destination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
) {
    StitchBottomNavigation(
        selectedLabel = when (destination) {
            AppDestination.CreateReport -> "Reportar"
            AppDestination.MyReports -> "Mis reportes"
            AppDestination.Tracking -> "Seguimiento"
            else -> "Mapa"
        },
        onMap = { onNavigate(AppDestination.Map) },
        onCreate = { onNavigate(AppDestination.CreateReport) },
        onMyReports = { onNavigate(AppDestination.MyReports) },
        onTracking = { onNavigate(AppDestination.Tracking) },
    )
}

@Composable
private fun AppDialogHost(
    dialog: AppDialog?,
    onDismiss: () -> Unit,
    onKeepDraft: () -> Unit,
    onDiscardDraft: () -> Unit,
    onDeactivate: () -> Unit,
    onAttachPhoto: () -> Unit,
    onPhotoInvalidType: () -> Unit,
    onPhotoTooLarge: () -> Unit,
    onPhotoPermissionDenied: () -> Unit,
    onSimulateContentError: () -> Unit,
    onFailNextSubmission: () -> Unit,
    onSimulateSessionExpiry: () -> Unit,
) {
    when (dialog) {
        null -> Unit
        AppDialog.Help -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Ayuda y estados de demostración") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Podés explorar reportes públicos, crear uno con cuenta o de forma anónima y consultar un código desde Seguimiento.")
                    TextButton(onClick = onSimulateContentError) { Text("Simular error de carga") }
                    TextButton(onClick = onFailNextSubmission) { Text("Simular error del próximo envío") }
                    TextButton(onClick = onSimulateSessionExpiry) { Text("Simular sesión vencida") }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        )
        AppDialog.Filters -> InformationalDialog(
            title = "Filtros",
            text = "Combiná estado, categoría y búsqueda. Los mismos resultados se muestran en el mapa y en la lista.",
            onDismiss = onDismiss,
        )
        is AppDialog.FutureFeature -> InformationalDialog(
            title = dialog.title,
            text = "Función prevista para una próxima versión. No se realizaron cambios.",
            onDismiss = onDismiss,
        )
        is AppDialog.DiscardDraft -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("¿Salir del borrador?") },
            text = { Text("Podés conservarlo para retomarlo después o descartarlo.") },
            confirmButton = { TextButton(onClick = onKeepDraft) { Text("Conservar") } },
            dismissButton = { TextButton(onClick = onDiscardDraft) { Text("Descartar") } },
        )
        AppDialog.DeactivateAccount -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Desactivar cuenta") },
            text = { Text("La cuenta se desactivará de forma lógica, se cerrarán sus sesiones y los reportes enviados se conservarán.") },
            confirmButton = { TextButton(onClick = onDeactivate) { Text("Desactivar") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        )
        AppDialog.PhotoPicker -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Adjuntar foto de demostración") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Elegí un resultado para recorrer los estados del selector sin acceder a archivos reales.")
                    TextButton(onClick = onAttachPhoto) { Text("Adjuntar foto válida") }
                    TextButton(onClick = onPhotoInvalidType) { Text("Simular tipo de archivo inválido") }
                    TextButton(onClick = onPhotoTooLarge) { Text("Simular archivo mayor a 5 MB") }
                    TextButton(onClick = onPhotoPermissionDenied) { Text("Simular permiso denegado") }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun UnavailableReportScreen(onBack: () -> Unit) {
    ScrollableFeatureScreen(
        title = "Reporte no disponible",
        onBack = onBack,
    ) {
        NoticeCard("El reporte ya no está disponible o fue retirado de la vista pública.")
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp),
        ) {
            Text("Volver")
        }
    }
}

@Composable
private fun InformationalDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}
