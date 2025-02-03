package com.bachnn.messenger.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.MessengerFragmentBinding
import com.bachnn.messenger.ui.viewModel.MessengerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessengerFragment : BaseFragment<MessengerViewModel, MessengerFragmentBinding>() {

    lateinit var userTo: User
    override fun createViewModel(): MessengerViewModel {
        return ViewModelProvider(this)[MessengerViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MessengerFragmentBinding {
        return MessengerFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        userTo = MessengerFragmentArgs.fromBundle(requireArguments()).userArg!!

        binding.messengerToolbar.title = userTo.name





    }
}