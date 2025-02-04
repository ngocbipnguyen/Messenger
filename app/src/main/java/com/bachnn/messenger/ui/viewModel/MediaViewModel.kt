package com.bachnn.messenger.ui.viewModel

import android.content.Context
import com.bachnn.messenger.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(@ApplicationContext val context: Context) : BaseViewModel(){



}