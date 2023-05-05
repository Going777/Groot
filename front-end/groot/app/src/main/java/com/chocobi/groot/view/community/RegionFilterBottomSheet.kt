package com.chocobi.groot.view.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.community.model.CommunityArticleListResponse
import com.chocobi.groot.view.community.model.CommunityService
import com.chocobi.groot.view.search.SearchDetailFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class RegionFilterBottomSheet(context: Context) : BottomSheetDialogFragment() {

    private lateinit var chipRegionGroup: ChipGroup
    private lateinit var regionList: ArrayList<String>
    private lateinit var regionFullList: ArrayList<String>

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

//        자동 완성 필터 눌렀을 때 처리
        autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                GlobalVariables.hideKeyboard(requireActivity())
//                3개 초과 불가
                if (chipRegionGroup.childCount == 3) {
                    Toast.makeText(requireContext(), "최대 3개까지 설정할 수 있습니다", Toast.LENGTH_SHORT)
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

        return rootView
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
        }
    }
}