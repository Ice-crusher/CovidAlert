package com.ice.covidalert.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.data.events.Event
import com.ice.domain.models.SickModel
import com.ice.domain.repositories.CredentialsRepo
import com.ice.domain.usecases.sick.SickUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sickUseCase: SickUseCase,
    private val schedulers: SchedulersProvider,
    private val credentialsRepo: CredentialsRepo
) : BaseViewModel() {

    val webViewLink = MutableLiveData<Event<String>>()

    private val compositeDisposable = CompositeDisposable()

    fun onCreate() {
        webViewLink.value = Event(credentialsRepo.getWebLink())
    }

    fun onClickSick() {
        _isLoading.value = Event(true)
        sickUseCase.execute(SickUseCase.Params(credentialsRepo.getUserId()))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                it?.let {
                    Log.d(MainViewModel::class.java.simpleName, "Success!")
                    _isLoading.value = Event(false)
                    _toastText.value = Event("Thanks for the support!")
                }
            }, {
                Log.e(MainViewModel::class.java.simpleName, it.toString())
                _isLoading.value = Event(false)
                _toastText.value = Event("Something went wrong!")
                // error
            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}