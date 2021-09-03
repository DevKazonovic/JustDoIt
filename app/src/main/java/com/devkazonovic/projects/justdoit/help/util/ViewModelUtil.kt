package com.devkazonovic.projects.justdoit.help.util

import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.domain.holder.Result

fun <T> handleResult(
    result: Result<T>,
    onSuccess: (data: T) -> Unit,
    onError: (message: Int) -> Unit,
) {
    when (result) {
        is Result.Success -> onSuccess(result.value)
        is Result.Failure -> {
            log(result.throwable.message.toString())
            onError(R.string.unKnownError)
        }
    }
}
