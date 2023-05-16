package com.chocobi.groot.data

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetDialog

@Suppress("DEPRECATION")
class CustomBottomSheetDialog(context: Context) : BottomSheetDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}