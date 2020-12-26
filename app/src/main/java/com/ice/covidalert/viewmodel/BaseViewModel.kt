package com.ice.covidalert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ice.data.events.Event

open class BaseViewModel : ViewModel() {

    protected val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    protected val _toastText = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastText
}