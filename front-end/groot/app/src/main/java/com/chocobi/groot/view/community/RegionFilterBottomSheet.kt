package com.chocobi.groot.view.community

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@Suppress("DEPRECATION")
class RegionFilterBottomSheet(context: Context, limitCnt: Int) : BottomSheetDialogFragment() {
    private val LIMITCNT = limitCnt
    private lateinit var chipRegionGroup: ChipGroup
    private lateinit var regionInfoText: TextView
    private lateinit var regionList: ArrayList<String>
    private lateinit var regionFullList: ArrayList<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView

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
        chipRegionGroup = rootView.findViewById(R.id.chipRegionGroup)

        findView(rootView)

//        자동완성으로 보여줄 내용들
        val regionNames =
            GlobalVariables.prefs.getString("region_names", "")?.split(", ") ?: emptyList()
        val items = regionNames.toTypedArray()

//        var autoCompleteTextView =
//            rootView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        var adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            items
        )
        autoCompleteTextView.setAdapter(adapter)

//        자동 완성 필터 눌렀을 때 처리
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
//                GlobalVariables.hideKeyboard(requireActivity())

//                bottomsheet 키보드 숨기기
                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                autoCompleteTextView.requestFocus() // 포커스를 설정합니다.
                inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)
                addChip(LIMITCNT, parent, position)
            }

        return rootView
    }

    private fun findView(view: View) {
        autoCompleteTextView =
            view.findViewById(R.id.autoCompleteTextView)
        regionInfoText = view.findViewById(R.id.regionInfoText)
        regionInfoText.text = "* 최대 ${LIMITCNT}개 지역까지 선택 가능합니다!"
    }

    private fun addChip(limitCnt: Int, parent: AdapterView<*>, position: Int) {
        if (chipRegionGroup.childCount == limitCnt) {
            Toast.makeText(requireContext(), "최대 ${limitCnt}개까지 설정할 수 있습니다", Toast.LENGTH_SHORT)
                .show()
            autoCompleteTextView.setText("")
        } else {
            val selectedItem = parent.getItemAtPosition(position).toString()
            val regionName = selectedItem.split(" ").last() // '동' 정보만 뽑아서 저장

            autoCompleteTextView.setText("")
            val isChipAlreadyExist = (0 until chipRegionGroup.childCount)
                .map { chipRegionGroup.getChildAt(it) }
                .filterIsInstance<Chip>()
                .any { it.tag == selectedItem }
            if (isChipAlreadyExist) {
                Toast.makeText(requireContext(), "이미 추가된 지역입니다", Toast.LENGTH_SHORT).show()
            } else {
                chipRegionGroup.addView(
                    Chip(
                        requireContext(),
                        null,
                        R.style.REGION_CHIP_ICON
                    ).apply {
                        text = regionName // chip 텍스트 설정
                        tag = selectedItem // chip 태그 설정
                        isCloseIconVisible = true // chip에서 X 버튼 보이게 하기
                        setOnCloseIconClickListener { chipRegionGroup.removeView(this) } // X버튼 누르면 chip 없어지게 하기
                    })
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        지역 필터 설정 완료 버튼 누르기 -> 데이터 전달
        view?.findViewById<Button>(R.id.regionFilterBtn)?.setOnClickListener {
            dismiss()
            regionList = ArrayList()
            regionFullList = ArrayList()
            for (i: Int in 1..chipRegionGroup.childCount) {
                val chip: Chip = chipRegionGroup.getChildAt(i - 1) as Chip
                regionList.add(chip.text.toString())
                regionFullList.add(chip.getTag().toString())
            }
            if (LIMITCNT == 3) {
                //            데이터 전달
                val bundle = Bundle().apply {
                    putStringArrayList("region_list", regionList)
                    putStringArrayList("region_full_list", regionFullList)
                }
                val passBundleBFragment = CommunityFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, passBundleBFragment)
                    .commit()
            } else if(LIMITCNT == 1) {
                val bundle = Bundle().apply {
                    putStringArrayList("region_full_list", regionFullList)
                }
                val passBundleBFragment = CommunityShareFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fl_container, passBundleBFragment)
                    .commit()

            }
        }
    }
}