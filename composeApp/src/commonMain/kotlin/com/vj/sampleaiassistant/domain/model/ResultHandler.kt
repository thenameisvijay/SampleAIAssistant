package com.vj.sampleaiassistant.domain.model

/**
 * Created by Vijay on 02/03/2026.
 * https://github.com/thenameisvijay
 */
sealed class ResultHandler<out T>{
    data class Success<T>(val data:T):ResultHandler<T>()
    data class Error(val exception: Exception, val message: String? = null):ResultHandler<Nothing>()
    object Loading : ResultHandler<Nothing>()
}

inline fun <T> ResultHandler<T>.onSuccess(action: (T) -> Unit): ResultHandler<T> {
    if (this is ResultHandler.Success) action(data)
    return this
}

inline fun <T> ResultHandler<T>.onError(action: (Throwable) -> Unit): ResultHandler<T> {
    if (this is ResultHandler.Error) action(exception)
    return this
}