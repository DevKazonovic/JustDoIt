package com.devkazonovic.projects.mytasks.domain.holder

sealed class DataState<T> {
    data class ErrorState<T>(val messageID: Int) : DataState<T>()
    data class ToastState<T>(val messageID: Int) : DataState<T>()

}