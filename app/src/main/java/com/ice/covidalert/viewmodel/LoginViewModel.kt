package com.ice.covidalert.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.ice.covidalert.rx.SchedulersProvider
import com.ice.data.events.Event
import com.ice.domain.models.SickModel
import com.ice.domain.repositories.CredentialsRepo
import com.ice.domain.usecases.login.LoginUseCase
import com.ice.domain.usecases.sick.SickUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val schedulers: SchedulersProvider,
    private val credentialsRepo: CredentialsRepo
) : BaseViewModel() {

    private val compositeDisposable = CompositeDisposable()

    protected val _isSuccessLogin = MutableLiveData<Event<Boolean>>()
    val isSuccessLogin: LiveData<Event<Boolean>>
        get() = _isSuccessLogin

    fun onCreate() {
        if (credentialsRepo.getEmail().isNotEmpty()
            && credentialsRepo.getUserId().isNotEmpty()
            && credentialsRepo.getFCMToken().isNotEmpty()) {
            _isLoading.value = Event(true)
            loginUseCase.execute(
                LoginUseCase.Params(credentialsRepo.getEmail(), credentialsRepo.getFCMToken())
            )
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({
                    it?.let {
                        Log.d(TAG, "Success Login Call!")
                        credentialsRepo.setEmail(it.email)
                        credentialsRepo.setFCMToken(it.fcmToken)
                        credentialsRepo.setUserId(it.userId)
                        credentialsRepo.setWebLink(it.mainWebSiteUrl)
                        _isSuccessLogin.value = Event(true)
                        _isLoading.value = Event(false)
                    }
                }, {
                    Log.e(TAG, it.stackTrace.toString())
                    _isSuccessLogin.value = Event(false)
                    _isLoading.value = Event(false)
                    _toastText.value = Event("You are logged out")
                }).let {
                    compositeDisposable.add(it)
                }
        }
    }

    fun login(email: String) {
        var fcmToken = credentialsRepo.getFCMToken()
        if (fcmToken.isNotEmpty()) {
            loginCall(email, fcmToken)
        } else {
            Log.d(TAG, "TRY GET FCM TOKEN..")

            // todo replace with USECASE
            FirebaseMessaging.getInstance().token.addOnCompleteListener{ task ->
                if (!task.isSuccessful) {
                    Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                fcmToken = task.result.toString()
                Log.d(TAG, "New Token is: $fcmToken")
                credentialsRepo.setFCMToken(fcmToken)
                loginCall(email, fcmToken)
            }
        }
    }

    private fun loginCall(email: String, fcmToken: String) {
        // todo show loading

        _isLoading.value = Event(true)
        loginUseCase.execute(LoginUseCase.Params(email, fcmToken))
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe({
                it?.let {
                    Log.d(TAG, "Success Login Call!")
                    credentialsRepo.setEmail(it.email)
                    credentialsRepo.setFCMToken(it.fcmToken)
                    credentialsRepo.setUserId(it.userId)
                    credentialsRepo.setWebLink(it.mainWebSiteUrl)
                    _isSuccessLogin.value = Event(true)
                    _isLoading.value = Event(false)
                }
            }, {
                Log.e(TAG, it.stackTrace.toString())
                _isSuccessLogin.value = Event(false)
                _isLoading.value = Event(false)
                _toastText.value = Event("Failed log in")
            }).let {
                compositeDisposable.add(it)
            }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}