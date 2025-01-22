package com.bachnn.messenger.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<MV: BaseViewModel, VB :ViewBinding>: Fragment() {

    protected lateinit var viewModel: MV

    protected lateinit var binding: VB

    protected abstract fun createViewModel(): MV

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected abstract fun initView()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = createViewModel()

        initView()
    }



}