package com.ice.covidalert.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.domain.models.SickModel
import com.ice.domain.repositories.CredentialsRepo
import com.ice.domain.usecases.sick.SickUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val sickUseCase: SickUseCase,
    private val schedulers: SchedulersProvider,
    private val credentialsRepo: CredentialsRepo
) : ViewModel() {

    val sickLiveData = MutableLiveData<SickModel>()
    private val compositeDisposable = CompositeDisposable()

    fun sick() {
        sickUseCase.execute(SickUseCase.Params(credentialsRepo.getUserId()))
            .subscribeOn(schedulers.io())
            .subscribe({
                it?.let {
                    Log.d(MainViewModel::class.java.simpleName, "Success!")
//                    sickLiveData.postValue(it)
                }
            }, {
                Log.e(MainViewModel::class.java.simpleName, it.toString())
                // error
            }).let {
                compositeDisposable.add(it)
            }
    }

    

    override fun onCleared() {
        compositeDisposable.clear()
    }
}