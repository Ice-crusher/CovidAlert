package com.ice.covidalert.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.ice.covidalert.databinding.ActivityMenuBinding
import com.ice.covidalert.di.obtainViewModel
import com.ice.covidalert.ui.HistoryAdapter
import com.ice.covidalert.ui.common.NearbyActivity
import com.ice.covidalert.ui.dialogs.SickDialog
import com.ice.covidalert.viewmodel.MenuViewModel
import com.ice.domain.models.HistoryModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_menu.view.*
import javax.inject.Inject

class MenuActivity: NearbyActivity() {
    var TAG = "MenuActivity"

    private lateinit var binding: ActivityMenuBinding
    private lateinit var historyAdapter: HistoryAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setActionBar(view.my_toolbar)
        viewModel = viewModelFactory.obtainViewModel(this)

        binding.buttonGotSick.setOnClickListener {
            SickDialog.newInstance(this, View.OnClickListener {
                viewModel.onClickSick()
            })?.show()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.onUpdateHistory()
        }

        binding.buttonNews.setOnClickListener {
            startActivity(NewsActivity.getIntent(this))
        }

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

        viewModel.items.observe(this,
            Observer {
                if (it.isEmpty()) {
                    binding.textEmptyList.visibility = View.VISIBLE
                } else {
                    binding.textEmptyList.visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
                historyAdapter.setItems(it)
            }
        )

        historyAdapter = HistoryAdapter()
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewHistory.adapter = historyAdapter
        historyAdapter.setClickListener(object : HistoryAdapter.OnClickItemListener {
            override fun onClick(item: HistoryModel.HistoryItem) {
                val label = "Infected contact"
                val uriBegin = "geo:${item.geographicCoordinateX},${item.geographicCoordinateY}"
                val query: String = "${item.geographicCoordinateX},${item.geographicCoordinateY}(${label})"
                val encodedQuery = Uri.encode(query)
                val uriString = "$uriBegin?q=$encodedQuery&z=16"
                val uri = Uri.parse(uriString)
                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps")

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent)
            }
        })

        requestPermissionsIfNeeded()
        viewModel.onCreate()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MenuActivity::class.java)
        }
    }

}