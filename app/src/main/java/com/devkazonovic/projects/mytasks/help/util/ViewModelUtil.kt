package com.devkazonovic.projects.mytasks.help.util

import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.help.holder.Result


fun <T> handleResult(
    result: Result<T>,
    onSuccess: (data: T) -> Unit,
    onError: (message: Int) -> Unit
) {
    when (result) {
        is Result.Success -> onSuccess(result.value)
        is Result.Failure -> onError(R.string.unKnownError)
    }
}
