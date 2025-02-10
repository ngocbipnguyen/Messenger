package com.bachnn.messenger.ui.view

import android.content.Context

fun interface IEmoticonCellViewFactory {

    fun newInstance(context: Context): EmoticonCellView

}