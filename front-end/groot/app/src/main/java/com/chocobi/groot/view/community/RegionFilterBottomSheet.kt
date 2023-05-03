package com.chocobi.groot.view.community

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.chocobi.groot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RegionFilterBottomSheet(context: Context) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.bottom_sheet_region_filter, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.selectRegionBtn)?.setOnClickListener {
            dismiss()
        }
    }
}