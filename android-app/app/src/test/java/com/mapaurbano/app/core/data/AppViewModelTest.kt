package com.mapaurbano.app.core.data

import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.core.navigation.AppDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun anonymousSubmissionCreatesConfirmationAndTrackingCode() = runTest(dispatcher) {
        val viewModel = AppViewModel()
        val category = viewModel.uiState.value.categories.first()
        viewModel.navigate(AppDestination.CreateReport)

        viewModel.updateDraft {
            it.copy(
                title = "Bache peligroso",
                description = "Ocupa media calzada",
                categoryId = category.id,
                submissionMode = SubmissionMode.Anonymous,
            )
        }
        viewModel.submitReport()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.destination is AppDestination.Confirmation)
        assertNotNull(state.report(state.createdReportId)?.trackingCode)
    }

    @Test
    fun accountModeRequestsLoginWithoutLosingDraft() = runTest(dispatcher) {
        val viewModel = AppViewModel()
        viewModel.updateDraft { it.copy(title = "Luminaria apagada") }

        viewModel.setSubmissionMode(SubmissionMode.Account)

        assertTrue(viewModel.uiState.value.destination is AppDestination.Login)
        assertEquals("Luminaria apagada", viewModel.uiState.value.draft.title)
    }

    @Test
    fun trackingAcceptsCodeWithoutSeparators() = runTest(dispatcher) {
        val viewModel = AppViewModel()
        viewModel.setTrackingQuery("7f2k9b1m4x3p")

        viewModel.trackReport()
        advanceUntilIdle()

        assertEquals("REP-2048", viewModel.uiState.value.trackedReportId)
    }

    @Test
    fun sessionExpiryDuringAccountSubmissionDoesNotCreateOrphanReport() = runTest(dispatcher) {
        val viewModel = AppViewModel()
        viewModel.login("vecina@correo.com", "clave")
        val initialCount = viewModel.uiState.value.reports.size
        val category = viewModel.uiState.value.categories.first()
        viewModel.navigate(AppDestination.CreateReport)
        viewModel.setSubmissionMode(SubmissionMode.Account)
        viewModel.updateDraft {
            it.copy(title = "Semáforo intermitente", categoryId = category.id)
        }

        viewModel.submitReport()
        viewModel.simulateSessionExpiry()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.destination is AppDestination.Login)
        assertEquals(initialCount, state.reports.size)
        assertEquals("Semáforo intermitente", state.draft.title)
    }

    @Test
    fun repeatedSubmitWhilePendingCreatesOnlyOneReport() = runTest(dispatcher) {
        val viewModel = AppViewModel()
        val initialCount = viewModel.uiState.value.reports.size
        val category = viewModel.uiState.value.categories.first()
        viewModel.navigate(AppDestination.CreateReport)
        viewModel.updateDraft {
            it.copy(title = "Árbol caído", categoryId = category.id)
        }

        viewModel.submitReport()
        viewModel.submitReport()
        advanceUntilIdle()

        assertEquals(initialCount + 1, viewModel.uiState.value.reports.size)
    }
}
