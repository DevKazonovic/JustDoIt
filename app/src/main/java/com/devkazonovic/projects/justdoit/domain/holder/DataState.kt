package com.devkazonovic.projects.justdoit.domain.holder

sealed class DataState<T> {
    data class ErrorState<T>(val messageID: Int) : DataState<T>()
    data class ToastState<T>(val messageID: Int) : DataState<T>()

}
