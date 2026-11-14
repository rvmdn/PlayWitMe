package com.playwithme.assistant.core.common

/**
 * A generic sealed class representing the result of an operation.
 *
 * Use this across all layers (Repository → UseCase → ViewModel) to model
 * success, error, and loading states in a type-safe way.
 *
 * @param T The type of data in the success case.
 */
sealed class Result<out T> {

    /** Operation completed successfully with [data]. */
    data class Success<out T>(val data: T) : Result<T>()

    /** Operation failed with [exception] and optional [message]. */
    data class Error(
        val exception: Throwable,
        val message: String = exception.localizedMessage ?: "Unknown error"
    ) : Result<Nothing>()

    /** Operation is in progress. */
    data object Loading : Result<Nothing>()

    // ─── Utility extensions ───────────────────────────────────────────────

    /** Returns true if this is [Success]. */
    val isSuccess: Boolean get() = this is Success

    /** Returns true if this is [Error]. */
    val isError: Boolean get() = this is Error

    /** Returns true if this is [Loading]. */
    val isLoading: Boolean get() = this is Loading

    /** Returns the data if [Success], otherwise null. */
    fun getOrNull(): T? = if (this is Success) data else null

    /** Returns the data if [Success], otherwise throws the exception. */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> error("Cannot get value from Loading state")
    }

    /**
     * Maps the [Success] data to a new type [R].
     * Error and Loading states are propagated as-is.
     */
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    /**
     * Executes [block] if this is [Success].
     */
    inline fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) block(data)
        return this
    }

    /**
     * Executes [block] if this is [Error].
     */
    inline fun onError(block: (Throwable) -> Unit): Result<T> {
        if (this is Error) block(exception)
        return this
    }

    /**
     * Executes [block] if this is [Loading].
     */
    inline fun onLoading(block: () -> Unit): Result<T> {
        if (this is Loading) block()
        return this
    }
}

/**
 * Wraps a suspend [block] in a try-catch, returning [Result.Success] on success
 * or [Result.Error] on exception.
 *
 * Usage:
 * ```kotlin
 * val result = safeCall { repository.fetchHero(id) }
 * ```
 */
suspend fun <T> safeCall(block: suspend () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}
