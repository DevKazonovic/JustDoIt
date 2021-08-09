package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ValidationViewModel @Inject constructor() : ViewModel() {

    private val _isCategoryNameEntered = MutableLiveData(false)
    private val _isTaskTitleEntered = MutableLiveData(false)

    fun taskInputValidation(title: String) {
        _isTaskTitleEntered.value = title.isNotEmpty() && title.isNotBlank()
    }

    fun categoryInputValidation(title: String) {
        _isCategoryNameEntered.value = title.isNotEmpty() && title.isNotBlank()
    }

    fun reset() {
        onCleared()
    }

    val isCategoryNameEntered: LiveData<Boolean> get() = _isCategoryNameEntered
    val isTaskTitleEntered: LiveData<Boolean> get() = _isTaskTitleEntered
}