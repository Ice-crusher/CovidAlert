package com.ice.covidalert.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ice.covidalert.databinding.ActivityNewsBinding
import com.ice.covidalert.di.obtainViewModel
import com.ice.covidalert.ui.common.NearbyActivity
import com.ice.covidalert.ui.dialogs.SickDialog
import com.ice.covidalert.viewmodel.NewsViewModel
import com.ice.covidalert.web.CustomWebViewClient
import dagger.android.AndroidInjection
import javax.inject.Inject


class NewsActivity: NearbyActivity() {
    var TAG = "NewsActivity"

    private lateinit var binding: ActivityNewsBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel : NewsViewModel

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = viewModelFactory.obtainViewModel(this)

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

        viewModel.webViewLink.observe(this,
            Observer { link ->
                link.getContentIfNotHandled()?.let {
                    // Set web view
                    binding.mainContentWebView.settings.javaScriptEnabled = true
                    binding.mainContentWebView.webViewClient = CustomWebViewClient(it)
                    binding.mainContentWebView.loadUrl(it)
                }
            })

        binding.button.setOnClickListener {
            SickDialog.newInstance(this@NewsActivity, View.OnClickListener {
                    viewModel.onClickSick()
                })?.show()
        }

        requestPermissionsIfNeeded()
        viewModel.onCreate()
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.mainContentWebView.canGoBack()) {
            binding.mainContentWebView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, NewsActivity::class.java)
        }
    }
}