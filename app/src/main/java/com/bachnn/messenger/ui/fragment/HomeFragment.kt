package com.bachnn.messenger.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.HomeFragmentBinding
import com.bachnn.messenger.ui.adapter.HomeAdapter
import com.bachnn.messenger.ui.notification.PushNotification
import com.bachnn.messenger.ui.viewModel.HomeViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, HomeFragmentBinding>() {


    private var listUsers = ArrayList<User>()

    lateinit var adapter : HomeAdapter

    private val notificationPermissionLaunch = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->

        }

    override fun createViewModel(): HomeViewModel {
        return ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): HomeFragmentBinding {
        return HomeFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                notificationPermissionLaunch.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        binding.navigationHome.setNavigationItemSelectedListener { item ->
            binding.drawerHome.closeDrawers()
            throw UnsupportedOperationException("Not yet implemented")
        }


        binding.iconToolbar.setOnClickListener {
            binding.drawerHome.open()
        }

        adapter = HomeAdapter(listUsers, onClickUser = {
            val action = HomeFragmentDirections.actionHomeFragmentToMessengerFragment()
            action.userArg = it
            binding.root.findNavController().navigate(action)

        })

        binding.recyclerHome.adapter = adapter

        viewModel.users.observe(this, Observer {
            if (it.isNotEmpty()) {
                listUsers.clear()
                listUsers.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                Log.w("FirebaseMessaging", "Fetching FCM registration token failed", it.exception)
            }

            val token = it.result
            viewModel.sendRegistrationToServer(token)
        })

        PushNotification.createNotificationChannel(requireContext())

    }
}