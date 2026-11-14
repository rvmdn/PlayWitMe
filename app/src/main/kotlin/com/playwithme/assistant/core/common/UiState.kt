package com.playwithme.assistant.core.common

/**
 * Represents the state of a UI screen or component.
 *
 * Use in ViewModels combined with [StateFlow] to drive Compose UI rendering.
 *
 * @param T The type of data displayed in the [Success] state.
 */
sealed class UiState<out T> {

    /** Initial state — no data loaded yet. */
    data object Idle : UiState<Nothing>()

    /** Data is being loaded. */
    data object Loading : UiState<Nothing>()

    /** Data loaded successfully. */
    data class Success<out T>(val data: T) : UiState<T>()

    /** An error occurred. */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : UiState<Nothing>()

    // ─── Utility ──────────────────────────────────────────────────────────

    val isIdle: Boolean get() = this is Idle
    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = if (this is Success) data else null
}

/**
 * Converts a [Result] into a [UiState].
 */
fun <T> Result<T>.toUiState(): UiState<T> = when (this) {
    is Result.Success -> UiState.Success(data)
    is Result.Error -> UiState.Error(message, exception)
    is Result.Loading -> UiState.Loading
}
