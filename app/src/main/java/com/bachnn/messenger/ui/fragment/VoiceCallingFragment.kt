package com.bachnn.messenger.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.VoiceCallingFragmentBinding
import com.bachnn.messenger.ui.viewModel.VoiceCallingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceCallingFragment: BaseFragment<VoiceCallingViewModel, VoiceCallingFragmentBinding>() {

    lateinit var userTo: User

    override fun createViewModel(): VoiceCallingViewModel {
        return ViewModelProvider(this)[VoiceCallingViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VoiceCallingFragmentBinding {
        return VoiceCallingFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        userTo = VoiceCallingFragmentArgs.fromBundle(requireArguments()).userArg!!
    }
}