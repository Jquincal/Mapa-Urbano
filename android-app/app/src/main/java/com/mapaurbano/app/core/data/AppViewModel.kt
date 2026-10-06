package com.mapaurbano.app.core.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapaurbano.app.core.model.Category
import com.mapaurbano.app.core.model.ContentLoadState
import com.mapaurbano.app.core.model.MapPoint
import com.mapaurbano.app.core.model.MapPresentation
import com.mapaurbano.app.core.model.PhotoIssue
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.ReportDraft
import com.mapaurbano.app.core.model.ReportFilters
import com.mapaurbano.app.core.model.ReportStatus
import com.mapaurbano.app.core.model.StatusEvent
import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.core.model.User
import com.mapaurbano.app.core.navigation.AppDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface AppDialog {
    data object Help : AppDialog
    data object Filters : AppDialog
    data class FutureFeature(val title: String) : AppDialog
    data class DiscardDraft(val target: AppDestination) : AppDialog
    data object DeactivateAccount : AppDialog
    data object PhotoPicker : AppDialog
}

data class AppUiState(
    val destination: AppDestination = AppDestination.Map,
    val backStack: List<AppDestination> = emptyList(),
    val reports: List<Report> = DemoContent.reports,
    val categories: List<Category> = DemoContent.categories,
    val user: User? = null,
    val draft: ReportDraft = ReportDraft(),
    val mapFilters: ReportFilters = ReportFilters(),
    val myReportsFilters: ReportFilters = ReportFilters(),
    val mapPresentation: MapPresentation = MapPresentation.Map,
    val createdReportId: String? = null,
    val trackingQuery: String = "",
    val trackedReportId: String? = null,
    val trackingError: String? = null,
    val isTracking: Boolean = false,
    val contentLoadState: ContentLoadState = ContentLoadState.Ready,
    val myReportsLoadState: ContentLoadState = ContentLoadState.Ready,
    val isSubmitting: Boolean = false,
    val failNextSubmission: Boolean = false,
    val formError: String? = null,
    val authError: String? = null,
    val selectedReportId: String? = null,
    val dialog: AppDialog? = null,
    val message: String? = null,
) {
    val visibleReports: List<Report>
        get() {
            val normalized = mapFilters.query.trim().lowercase()
            return reports.filter { report ->
                (mapFilters.status == null || report.status == mapFilters.status) &&
                    (mapFilters.categoryId == null || report.category.id == mapFilters.categoryId) &&
                    (normalized.isBlank() || listOf(
                        report.title,
                        report.description,
                        report.location,
                        report.category.label,
                    ).any { normalized in it.lowercase() })
            }
        }

    val ownedReports: List<Report>
        get() = user?.let { current -> reports.filter { it.ownerId == current.id } }.orEmpty()

    fun report(id: String?): Report? = reports.firstOrNull { it.id == id }
}

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    private var submissionGeneration = 0L

    fun navigate(destination: AppDestination, replace: Boolean = false) {
        val state = _uiState.value
        if (state.isSubmitting) {
            _uiState.update { it.copy(message = "Esperá a que termine el envío antes de salir.") }
            return
        }
        if (destination == AppDestination.MyReports && state.user == null) {
            navigate(AppDestination.Login(AppDestination.MyReports))
            return
        }
        if (destination == AppDestination.Profile && state.user == null) {
            navigate(AppDestination.Login(AppDestination.Profile))
            return
        }
        if (state.destination == AppDestination.CreateReport &&
            state.draft.isDirty &&
            destination != AppDestination.CreateReport &&
            destination !is AppDestination.Login &&
            destination != AppDestination.LocationPicker
        ) {
            _uiState.update { it.copy(dialog = AppDialog.DiscardDraft(destination)) }
            return
        }
        _uiState.update {
            it.copy(
                destination = destination,
                backStack = when {
                    replace -> it.backStack
                    destination == AppDestination.Map -> emptyList()
                    destination == AppDestination.CreateReport ||
                        destination == AppDestination.MyReports ||
                        destination == AppDestination.Tracking -> emptyList()
                    else -> it.backStack + it.destination
                },
                dialog = null,
                message = null,
                authError = null,
                selectedReportId = if (destination == AppDestination.Map) null else it.selectedReportId,
            )
        }
    }

    fun back() {
        val state = _uiState.value
        if (state.dialog != null) {
            dismissDialog()
            return
        }
        if (state.isSubmitting) {
            _uiState.update { it.copy(message = "Esperá a que termine el envío antes de salir.") }
            return
        }
        if (state.destination == AppDestination.CreateReport && state.draft.isDirty) {
            _uiState.update { it.copy(dialog = AppDialog.DiscardDraft(AppDestination.Map)) }
            return
        }
        if (state.destination is AppDestination.Confirmation) {
            _uiState.update { it.copy(destination = AppDestination.Map, backStack = emptyList()) }
            return
        }
        val previous = state.backStack.lastOrNull() ?: AppDestination.Map
        _uiState.update { it.copy(destination = previous, backStack = it.backStack.dropLast(1)) }
    }

    fun setMapPresentation(value: MapPresentation) = _uiState.update { it.copy(mapPresentation = value) }
    fun setQuery(value: String) = _uiState.update { it.copy(mapFilters = it.mapFilters.copy(query = value)) }
    fun setStatusFilter(value: ReportStatus?) = _uiState.update { it.copy(mapFilters = it.mapFilters.copy(status = value)) }
    fun setCategoryFilter(value: String?) = _uiState.update { it.copy(mapFilters = it.mapFilters.copy(categoryId = value)) }
    fun clearFilters() = _uiState.update { it.copy(mapFilters = ReportFilters(), dialog = null) }

    fun selectReportOnMap(report: Report) = _uiState.update {
        it.copy(selectedReportId = report.id)
    }

    fun setMyReportsQuery(value: String) = _uiState.update {
        it.copy(myReportsFilters = it.myReportsFilters.copy(query = value))
    }

    fun setMyReportsStatus(value: ReportStatus?) = _uiState.update {
        it.copy(myReportsFilters = it.myReportsFilters.copy(status = value))
    }

    fun setMyReportsCategory(value: String?) = _uiState.update {
        it.copy(myReportsFilters = it.myReportsFilters.copy(categoryId = value))
    }

    fun clearMyReportsFilters() = _uiState.update { it.copy(myReportsFilters = ReportFilters()) }

    fun updateDraft(transform: (ReportDraft) -> ReportDraft) = _uiState.update {
        if (it.isSubmitting) it else it.copy(draft = transform(it.draft), formError = null)
    }

    fun setSubmissionMode(mode: SubmissionMode) {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(draft = it.draft.copy(submissionMode = mode)) }
        if (mode == SubmissionMode.Account && _uiState.value.user == null) {
            navigate(AppDestination.Login(AppDestination.CreateReport))
        }
    }

    fun confirmLocation(point: MapPoint, label: String) {
        _uiState.update {
            it.copy(
                draft = it.draft.copy(point = point.copy(label = label), location = label),
                destination = AppDestination.CreateReport,
                backStack = it.backStack.dropLast(1),
            )
        }
    }

    fun submitReport() {
        val state = _uiState.value
        if (state.isSubmitting) return
        if (!state.draft.canSubmit) {
            _uiState.update { it.copy(formError = "Completá el título, la categoría y la ubicación.") }
            return
        }
        if (state.draft.submissionMode == SubmissionMode.Account && state.user == null) {
            navigate(AppDestination.Login(AppDestination.CreateReport))
            return
        }
        val submittedDraft = state.draft
        val submittedUser = state.user
        val submittedCategories = state.categories
        val submissionId = ++submissionGeneration
        _uiState.update { it.copy(isSubmitting = true, formError = null) }
        viewModelScope.launch {
            delay(650)
            if (submissionId != submissionGeneration) return@launch
            val latest = _uiState.value
            if (latest.failNextSubmission) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        failNextSubmission = false,
                        formError = "No pudimos confirmar si el reporte se envió. El borrador sigue guardado: revisá la conexión antes de reintentar.",
                    )
                }
                return@launch
            }
            val sessionIsStillValid = submittedDraft.submissionMode == SubmissionMode.Anonymous ||
                latest.user?.id == submittedUser?.id
            if (latest.destination != AppDestination.CreateReport || !sessionIsStillValid) {
                _uiState.update { it.copy(isSubmitting = false) }
                return@launch
            }
            val category = submittedCategories.firstOrNull { it.id == submittedDraft.categoryId }
                ?: run {
                    _uiState.update { it.copy(isSubmitting = false) }
                    return@launch
                }
            val sequence = latest.reports.size + 2050
            val isAnonymous = submittedDraft.submissionMode == SubmissionMode.Anonymous
            val report = Report(
                id = "REP-$sequence",
                title = submittedDraft.title.trim(),
                description = submittedDraft.description.trim().ifBlank { "Sin descripción adicional." },
                category = category,
                status = ReportStatus.Pending,
                location = submittedDraft.location,
                point = submittedDraft.point,
                createdAtLabel = "hace un momento",
                submissionMode = submittedDraft.submissionMode,
                ownerId = if (isAnonymous) null else submittedUser?.id,
                trackingCode = if (isAnonymous) opaqueCode(sequence) else null,
                hasPhoto = submittedDraft.hasPhoto,
                history = listOf(
                    StatusEvent(
                        status = ReportStatus.Pending,
                        title = "Reporte recibido",
                        description = "El municipio recibió la incidencia y la revisará.",
                        dateLabel = "Ahora",
                    ),
                ),
            )
            _uiState.update {
                it.copy(
                    reports = listOf(report) + it.reports,
                    createdReportId = report.id,
                    draft = ReportDraft(),
                    destination = AppDestination.Confirmation(report.id),
                    backStack = emptyList(),
                    isSubmitting = false,
                    dialog = null,
                    message = null,
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (!email.contains('@') || password.length < 4) {
            _uiState.update { it.copy(authError = "Revisá el correo y la contraseña.") }
            return
        }
        finishAuthentication(DemoContent.user.copy(email = email.trim()))
    }

    fun register(displayName: String, email: String, password: String) {
        if (displayName.isBlank() || !email.contains('@') || password.length < 8) {
            _uiState.update {
                it.copy(authError = "Ingresá un nombre, un correo válido y una contraseña de 8 caracteres.")
            }
            return
        }
        finishAuthentication(User("demo-user", displayName.trim(), email.trim()))
    }

    private fun finishAuthentication(user: User) {
        val current = _uiState.value.destination
        val target = when (current) {
            is AppDestination.Login -> current.returnTo
            is AppDestination.Register -> current.returnTo
            else -> AppDestination.Map
        }
        _uiState.update {
            it.copy(
                user = user,
                destination = target,
                backStack = emptyList(),
                message = "Sesión iniciada.",
                authError = null,
            )
        }
    }

    fun logout() {
        submissionGeneration++
        _uiState.update {
            it.copy(
                user = null,
                destination = AppDestination.Map,
                backStack = emptyList(),
                trackedReportId = null,
                myReportsFilters = ReportFilters(),
                authError = null,
                formError = null,
                isSubmitting = false,
                message = "Sesión cerrada.",
                dialog = null,
            )
        }
    }

    fun setTrackingQuery(value: String) = _uiState.update {
        it.copy(trackingQuery = value, trackingError = null, trackedReportId = null)
    }

    fun trackReport() {
        if (_uiState.value.isTracking) return
        _uiState.update { it.copy(isTracking = true, trackingError = null, trackedReportId = null) }
        viewModelScope.launch {
            delay(450)
            val current = _uiState.value
            val query = normalizeCode(current.trackingQuery)
            val report = current.reports.firstOrNull {
                it.trackingCode != null && normalizeCode(it.trackingCode) == query
            }
            _uiState.update {
                it.copy(
                    isTracking = false,
                    trackedReportId = report?.id,
                    trackingError = if (report == null) {
                        "No pudimos consultar ese código. Revisalo e intentá nuevamente."
                    } else null,
                )
            }
        }
    }

    fun setPhoto(hasPhoto: Boolean, issue: PhotoIssue? = null) = _uiState.update {
        if (it.isSubmitting) it.copy(dialog = null) else it.copy(
            draft = it.draft.copy(hasPhoto = hasPhoto, photoIssue = issue),
            dialog = null,
            formError = null,
        )
    }

    fun simulateContentError() = _uiState.update {
        it.copy(
            contentLoadState = ContentLoadState.Error,
            myReportsLoadState = ContentLoadState.Error,
            dialog = null,
        )
    }

    fun retryContent() {
        _uiState.update {
            it.copy(
                contentLoadState = ContentLoadState.Loading,
                myReportsLoadState = ContentLoadState.Loading,
            )
        }
        viewModelScope.launch {
            delay(650)
            _uiState.update {
                it.copy(
                    contentLoadState = ContentLoadState.Ready,
                    myReportsLoadState = ContentLoadState.Ready,
                )
            }
        }
    }

    fun failNextSubmission() = _uiState.update {
        it.copy(
            failNextSubmission = true,
            dialog = null,
            message = "El próximo envío mostrará un error recuperable de conexión.",
        )
    }

    fun simulateSessionExpiry() {
        submissionGeneration++
        _uiState.update {
            it.copy(
                user = null,
                destination = AppDestination.Login(AppDestination.CreateReport),
                backStack = emptyList(),
                dialog = null,
                authError = "La sesión venció. Iniciá sesión nuevamente; el borrador se conservó.",
                isSubmitting = false,
            )
        }
    }

    fun focusReportOnMap(report: Report) = _uiState.update {
        it.copy(
            selectedReportId = report.id,
            mapFilters = ReportFilters(),
            mapPresentation = MapPresentation.Map,
            destination = AppDestination.Map,
            backStack = emptyList(),
        )
    }

    fun openTrackingFor(report: Report) {
        val code = report.trackingCode ?: return
        _uiState.update {
            it.copy(
                trackingQuery = code,
                trackedReportId = report.id,
                trackingError = null,
                destination = AppDestination.Tracking,
                backStack = listOf(AppDestination.Confirmation(report.id)),
            )
        }
    }

    fun showDialog(dialog: AppDialog) = _uiState.update { it.copy(dialog = dialog) }
    fun dismissDialog() = _uiState.update { it.copy(dialog = null) }
    fun consumeMessage() = _uiState.update { it.copy(message = null) }
    fun clearAuthError() = _uiState.update { it.copy(authError = null) }
    fun notify(message: String) = _uiState.update { it.copy(message = message) }

    fun discardDraftAndContinue() {
        val target = (_uiState.value.dialog as? AppDialog.DiscardDraft)?.target ?: AppDestination.Map
        _uiState.update {
            it.copy(
                draft = ReportDraft(),
                dialog = null,
                destination = target,
                backStack = emptyList(),
            )
        }
    }

    fun keepDraftAndContinue() {
        val target = (_uiState.value.dialog as? AppDialog.DiscardDraft)?.target ?: AppDestination.Map
        _uiState.update {
            it.copy(
                dialog = null,
                destination = target,
                backStack = emptyList(),
            )
        }
    }

    fun deactivateAccount() {
        submissionGeneration++
        _uiState.update {
            it.copy(
                user = null,
                destination = AppDestination.Map,
                backStack = emptyList(),
                myReportsFilters = ReportFilters(),
                trackedReportId = null,
                isSubmitting = false,
                dialog = null,
                message = "La cuenta de demostración fue desactivada.",
            )
        }
    }

    private fun normalizeCode(value: String) = value.filter(Char::isLetterOrDigit).uppercase()

    private fun opaqueCode(seed: Int): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val raw = buildString {
            var value = seed * 7919
            repeat(12) {
                append(alphabet[value.mod(alphabet.length)])
                value = (value * 37 + 17).ushr(1)
            }
        }
        return raw.chunked(4).joinToString("-")
    }
}
