package com.devkazonovic.projects.justdoit.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ValidationViewModel @Inject constructor() : ViewModel() {

    private val _isCategoryNameEntered = MutableLiveData(false)
    private val _isTaskTitleEntered = MutableLiveData(false)
    private val _isCategoryLengthMax = MutableLiveData(false)

    fun taskInputValidation(title: String) {
        _isTaskTitleEntered.value = title.isNotEmpty() && title.isNotBlank()
    }

    fun categoryInputValidation(title: String) {
        _isCategoryNameEntered.value =
            title.isNotEmpty() && title.isNotBlank() && title.length <= 50
        _isCategoryLengthMax.value = title.length > 50

    }

    fun reset() {
        onCleared()
    }

    val isCategoryNameEntered: LiveData<Boolean> get() = _isCategoryNameEntered
    val isTaskTitleEntered: LiveData<Boolean> get() = _isTaskTitleEntered
    val isCategoryLengthMax: LiveData<Boolean> get() = _isCategoryLengthMax

}