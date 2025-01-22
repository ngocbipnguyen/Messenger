package com.bachnn.messenger.ui.viewModel

import com.bachnn.messenger.base.BaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(private val auth: FirebaseAuth): BaseViewModel() {


    init {

    }


    fun isSignByGoogle() : Boolean {
        return auth.currentUser != null
    }


}