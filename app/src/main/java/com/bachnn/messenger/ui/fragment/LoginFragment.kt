package com.bachnn.messenger.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.databinding.LoginFragmentBinding
import com.bachnn.messenger.ui.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: BaseFragment<LoginViewModel, LoginFragmentBinding>() {
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

    }
}