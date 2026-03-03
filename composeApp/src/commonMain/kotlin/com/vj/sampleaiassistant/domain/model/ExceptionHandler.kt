package com.vj.sampleaiassistant.domain.model

/**
 * Created by Vijay on 03/03/2026.
 * https://github.com/thenameisvijay
 */
sealed class ExceptionHandler(message: String) : Exception(message) {
    data class NetworkException(override val cause: Throwable) : ExceptionHandler("Network error occurred")
    data class DatabaseException(override val cause: Throwable) : ExceptionHandler("Database error occurred")
    data class ValidationException(override val message: String) : ExceptionHandler(message)
    object UnknownException : ExceptionHandler("Unknown error occurred")
}