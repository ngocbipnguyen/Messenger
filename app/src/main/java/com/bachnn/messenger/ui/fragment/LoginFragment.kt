package com.bachnn.messenger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.databinding.LoginFragmentBinding
import com.bachnn.messenger.ui.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel, LoginFragmentBinding>() {
    override fun createViewModel(): LoginViewModel {
        return ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LoginFragmentBinding {
        return LoginFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {

        val controller = binding.root.findNavController()

        binding.loginView.setOnClickListener {
            //todo login and save data on firebase.
            lifecycleScope.launch {
                viewModel.signIn(requireContext(), doOnSuccess = {
                    val navOptions =
                        NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                    controller.navigate(
                        R.id.action_loginFragment_to_homeFragment, Bundle(), navOptions
                    )
                }, doOnError = {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_LONG).show()
                })
            }
        }

    }
}