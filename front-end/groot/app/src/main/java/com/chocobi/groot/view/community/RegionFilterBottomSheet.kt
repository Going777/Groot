package com.chocobi.groot.view.community

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.GlobalScope

@Suppress("DEPRECATION")
class RegionFilterBottomSheet(context: Context) : BottomSheetDialogFragment() {

    private lateinit var chipRegionGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.bottom_sheet_region_filter, container, false)

//        자동완성으로 보여줄 내용들
        val regionNames =
            GlobalVariables.prefs.getString("region_names", "")?.split(", ") ?: emptyList()
        val items = regionNames.toTypedArray()

        var autoCompleteTextView =
            rootView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        autoCompleteTextView.setAdapter(adapter)

        chipRegionGroup = rootView.findViewById(R.id.chipRegionGroup)

        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                GlobalVariables.hideKeyboard(requireActivity())

                val selectedItem = parent.getItemAtPosition(position).toString()
                val regionName = selectedItem.split(" ").last()

                autoCompleteTextView.setText("")
                chipRegionGroup.addView(Chip(requireContext()).apply {
                    text = regionName // chip 텍스트 설정
                    isCloseIconVisible = true // chip에서 X 버튼 보이게 하기
                    setOnCloseIconClickListener { chipRegionGroup.removeView(this) } // X버튼 누르면 chip 없어지게 하기
                })
            }

        val regionFilterBtn = rootView.findViewById<Button>(R.id.selectRegionBtn)

        regionFilterBtn.setOnClickListener {

        }


        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.selectRegionBtn)?.setOnClickListener {
            dismiss()
        }
    }




}