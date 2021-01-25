package com.ice.covidalert.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.data.events.Event
import com.ice.domain.models.HistoryModel
import com.ice.domain.repositories.CredentialsRepo
import com.ice.domain.usecases.history.HistoryUseCase
import com.ice.domain.usecases.sick.SickUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class MenuViewModel @Inject constructor(
    private val historyUseCase: HistoryUseCase,
    private val sickUseCase: SickUseCase,
    private val schedulers: SchedulersProvider,
    private val credentialsRepo: CredentialsRepo
) : BaseViewModel() {

    protected val _items = MutableLiveData<ArrayList<HistoryModel.HistoryItem>>()
    val items: LiveData<ArrayList<HistoryModel.HistoryItem>>
        get() = _items

    private val compositeDisposable = CompositeDisposable()

    fun onCreate() {
        onUpdateHistory()
    }

    fun onUpdateHistory() {
        historyUseCase.execute(HistoryUseCase.Params(credentialsRepo.getUserId()))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                if (it.history.isNotEmpty()) {
                    _items.value = it.history as ArrayList<HistoryModel.HistoryItem>
                }
            }, {
                _items.value = ArrayList()
            }).let {
                compositeDisposable.add(it)
            }
    }

    fun onClickSick() {
        _isLoading.value = Event(true)
        sickUseCase.execute(SickUseCase.Params(credentialsRepo.getUserId()))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                it?.let {
                    Log.d(MenuViewModel::class.java.simpleName, "Success!")
                    _isLoading.value = Event(false)
                    _toastText.value = Event("Thanks for the support!")
                }
            }, {
                Log.e(MenuViewModel::class.java.simpleName, it.toString())
                _isLoading.value = Event(false)
                _toastText.value = Event("Something went wrong!")
                // error
            }).let {
                compositeDisposable.add(it)
            }

    }


}