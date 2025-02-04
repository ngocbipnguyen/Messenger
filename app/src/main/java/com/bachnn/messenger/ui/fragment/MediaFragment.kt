package com.bachnn.messenger.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.databinding.MediaFragmentBinding
import com.bachnn.messenger.ui.viewModel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaFragment : BaseFragment<MediaViewModel, MediaFragmentBinding>() {
    override fun createViewModel(): MediaViewModel {
        return ViewModelProvider(this)[MediaViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaFragmentBinding {
        return MediaFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {
    }
}