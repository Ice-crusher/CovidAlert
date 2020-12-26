package com.ice.covidalert.ui.main

import android.os.Bundle
import android.text.Editable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ice.covidalert.databinding.ActivityLoginBinding
import com.ice.covidalert.databinding.ActivityMainBinding
import com.ice.covidalert.di.obtainViewModel
import com.ice.covidalert.ui.common.BaseActivity
import com.ice.covidalert.viewmodel.LoginViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.view.*
import javax.inject.Inject

class LoginActivity : BaseActivity() {
    companion object {
        const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        view.editTextEmail.text = Editable.Factory.getInstance().newEditable("email1@gmail.com")
        viewModel = viewModelFactory.obtainViewModel(this)

        view.buttonLogIn.setOnClickListener {
            viewModel.login(view.editTextEmail.text.toString())
        }

        viewModel.isSuccessLogin.observe(this,
            Observer { isSuccess ->
                if (isSuccess.peekContent()) {
                    startActivity(MainActivity.getIntent(this))
                    finish()
                } else {
                    // todo show error
                }
            })
        viewModel.isLoading.observe(this,
            Observer {
                if (it.peekContent()) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        )
        viewModel.toastMessage.observe(this,
            Observer {
                makeToast(it.peekContent())
            }
        )

        viewModel.onCreate()
    }



}