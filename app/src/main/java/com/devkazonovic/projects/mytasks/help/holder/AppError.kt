package com.devkazonovic.projects.mytasks.help.holder

sealed class AppError {
    data class GeneralError(val message: Int) : AppError()
    data class LoadError(val message: Int) : AppError()
    data class ConnectivityError(val message: Int) : AppError()

}
