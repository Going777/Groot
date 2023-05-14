package com.chocobi.groot.view.pot.adapter


import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.MainActivity
import com.chocobi.groot.R
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.PERMISSION_CAMERA
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.data.UserData
import com.chocobi.groot.view.pot.PlantBottomSheet
import com.chocobi.groot.view.pot.PotDiaryBottomSheet
import com.chocobi.groot.view.pot.model.DiaryListResponse
import com.chocobi.groot.view.pot.model.PotService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PotDiaryListRVAdapter(private val context: Context, private val mActivity: MainActivity) :
    RecyclerView.Adapter<DiaryItemViewHolder>() {

    private val TAG = "PotDiaryListRVAdapter"

    interface RecyclerViewAdapterDelegate {
        fun onLoadMore()
    }

    private var mutableList: MutableList<DiaryListResponse> = mutableListOf()

    var delegate: RecyclerViewAdapterDelegate? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryItemViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_diary_list_item, parent, false)
        return DiaryItemViewHolder(view)
    }

    interface ItemClickListener {
        fun onSpinnerBtnClick(view: View, position: Int)
    }

    private lateinit var spinnerBtnClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.spinnerBtnClickListner = itemClickListener


    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return mutableList.size
    }

    override fun onBindViewHolder(holder: DiaryItemViewHolder, position: Int) {
        holder.diaryListResponse = mutableList[position]
        val diary = holder.diaryListResponse.diary.content[0]
        val spinnerBtn = holder.itemView.findViewById<ImageButton>(R.id.spinnerButton)
        spinnerBtn.setOnClickListener {
            val diaryId = diary.id
            Log.d(TAG, "${diaryId}")
            var dialog = AlertDialog.Builder(context)
            dialog.setTitle("다이어리 설정")
            val dialogArray = arrayOf("수정", "삭제")

            dialog.setItems(dialogArray) { _, which ->
                when (which) {
                    0 -> {
//                        수정
                        val potBottomSheet = PotDiaryBottomSheet(
                            context,
                            diary.id,
                            diary.potId,
                            diary.potName,
                            diary.content,
                            diary.imgPath,
                            diary.water,
                            diary.pruning,
                            diary.bug,
                            diary.sun,
                            diary.nutrients
                        )
                        potBottomSheet.show(
                            mActivity.supportFragmentManager,
                            potBottomSheet.tag
                        )
                        spinnerBtnClickListner.onSpinnerBtnClick(it, position)
                    }

                    1 -> {
//                        삭제
                        deleteDiary(diaryId)
                        spinnerBtnClickListner.onSpinnerBtnClick(it, position)
                    }
                }
            }
            dialog.setNegativeButton(
                "취소",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            dialog.show()
        }

        holder.delegate = object : DiaryItemViewHolder.ItemViewHolderDelegate {
            override fun onItemViewClick(diaryListResponse: DiaryListResponse) {
                val context = holder.itemView.context
//                if (context is FragmentActivity) {
//                    val fragmentManager = context.supportFragmentManager
//                    val communityDetailFragment = CommunityDetailFragment()
//
//                    // articleId 값을 CommunityDetailFragment에 전달하기 위해 인수(bundle)를 설정합니다.
//                    val args = Bundle()
//                    args.putInt("articleId", diaryListResponse.articles.content[0].articleId)
//                    communityDetailFragment.arguments = args
//                    Log.d("CommunityDetailFragmentArticleId", communityDetailFragment.arguments.toString())
//
//                    fragmentManager.beginTransaction()
//                        .replace(R.id.fl_container, communityDetailFragment)
//                        .addToBackStack(null)
//                        .commit()
//                }
            }

        }


        holder.updateView()

        if (position == mutableList.size - 1) {
            delegate?.onLoadMore()
        }

    }

    fun reload(mutableList: MutableList<DiaryListResponse>) {
        this.mutableList.clear()
        this.mutableList.addAll(mutableList)
        notifyDataSetChanged()
    }

    fun loadMore(mutableList: MutableList<DiaryListResponse>) {
        this.mutableList.addAll(mutableList)
        notifyItemRangeChanged(this.mutableList.size - mutableList.size + 1, mutableList.size)
    }

    private fun deleteDiary(id: Int) {
        val retrofit = RetrofitClient.getClient()!!
        val potService = retrofit.create(PotService::class.java)
        val userPK = UserData.getUserPK()
        potService.requestDeleteDiary(id, userPK, null).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.code() == 200) {
                    Log.d(TAG, "다이어리 삭제 성공")
                } else {
                    Log.d(TAG, "다이어리 삭제 실패")
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                Log.d(TAG, "다이어리 삭제 요청도 실패")
            }
        })

    }

}