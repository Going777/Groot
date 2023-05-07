package com.chocobi.groot.view.pot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.chocobi.groot.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PotBottomSheet(context: Context) : BottomSheetDialogFragment()
{
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.bottom_sheet_pot, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
//        view?.findViewById<Button>(R.id.button_bottom_sheet)?.setOnClickListener {
//            dismiss()
//        }
    }
}