package com.bachnn.messenger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.databinding.SplashFragmentBinding
import com.bachnn.messenger.ui.viewModel.SplashViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment @Inject constructor(
) : BaseFragment<SplashViewModel, SplashFragmentBinding>() {
    override fun createViewModel(): SplashViewModel {
        return ViewModelProvider(this)[SplashViewModel::class.java]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): SplashFragmentBinding {
        return SplashFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        val navController = binding.root.findNavController()


        lifecycleScope.launch {
            delay(3000)

            if (!viewModel.isSignByGoogle()) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build()

                navController.navigate(
                    R.id.action_splashFragment_to_loginFragment,
                    Bundle(),
                    navOptions
                )
            } else {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.splashFragment, true)
                    .build()

                navController.navigate(
                    R.id.action_splashFragment_to_homeFragment,
                    Bundle(),
                    navOptions
                )
            }
        }

    }
}