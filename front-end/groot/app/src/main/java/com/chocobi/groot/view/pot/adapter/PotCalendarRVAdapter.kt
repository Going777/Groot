package com.chocobi.groot.view.pot.adapter


import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.BasicResponse
import com.chocobi.groot.data.RetrofitClient
import com.chocobi.groot.view.pot.model.Diary
import com.chocobi.groot.view.pot.model.DiaryRequest
import com.chocobi.groot.view.pot.model.PotService
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference

class PotCalendarRVAdapter(val context: Context, val items: List<Diary>) :
    RecyclerView.Adapter<PotCalendarRVAdapter.ViewHolder>() {

    private val TAG = "PotCalendarRVAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pot_calendar_item, parent, false)

        return ViewHolder(view)
    }

    interface ItemClickListener {
        fun onCheckClick(view: View, position: Int)
        fun onPotImgClick(view: View, position: Int)
    }

    private lateinit var checkClickListner: ItemClickListener
    private lateinit var potImgClickListner: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.checkClickListner = itemClickListener
        this.potImgClickListner = itemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItems(items[position])

        val checkBox = holder.itemView.findViewById<CheckBox>(R.id.checkbox)
        val potImg = holder.itemView.findViewById<ImageView>(R.id.potImg)
        checkBox.setOnClickListener {
            checkClickListner.onCheckClick(it, position)
            Log.d(
                TAG,
                "potId:${items[position].potId} / code:${items[position].code} / isChecked:${checkBox.isChecked}"
            )
            if (checkBox.isChecked) {
//                다이어리 쓰기
                var dialog = AlertDialog.Builder(
                    context,
                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                )
                dialog.setTitle("식물 미션")
                dialog.setMessage("화분에게 물을 주었나요?")
                dialog.setPositiveButton(
                    "완료",
                    DialogInterface.OnClickListener { dialog, which ->
                        postDiary(items[position].potId, items[position].code)
                        dialog.dismiss()
                    })
                dialog.setNegativeButton(
                    "취소",
                    DialogInterface.OnClickListener { dialog, which ->
                        checkBox.isChecked = false
                        dialog.dismiss()
                    })
                dialog.show()
            } else {
//                다이어리 삭제
                Toast.makeText(context, "이미 완료한 미션이에요.", Toast.LENGTH_SHORT).show()
                checkBox.isChecked = true
            }
        }
        potImg.setOnClickListener {
            potImgClickListner.onPotImgClick(it, position)
        }
    }

    //    전체 리사이클러뷰의 개수
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)
        fun bindItems(item: Diary) {
            val potName = itemView.findViewById<TextView>(R.id.potName)
            val potMission = itemView.findViewById<TextView>(R.id.potMission)
            val potImg = itemView.findViewById<CircleImageView>(R.id.potImg)
            potName.text = item.potName
            if (item.code == 0) {
                potMission.text = "물 주기"
            } else {
                potMission.text = "영양제"
            }
            val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
            if (item.done) {
                checkBox.isChecked = true
            }


            potImg.post {
                view.get()?.let {
                    ThreadUtil.startThread {
                        val futureTarget: FutureTarget<Bitmap> = Glide.with(it.context)
                            .asBitmap()
                            .load(item.imgPath)
                            .submit(potImg.width, potImg.height)

                        val bitmap = futureTarget.get()

                        ThreadUtil.startUIThread(0) {
                            potImg.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
    }
    private fun postDiary(potId: Int, code: Int) {
        val retrofit = RetrofitClient.getClient()!!
        val potService = retrofit.create(PotService::class.java)
        var water: Boolean = false
        var nutrients: Boolean = false
        var info: String = ""
        if (code == 0) {
            water = true
            info = "물 주기"
        } else {
            nutrients = true
            info = "영양제 주기"
        }

        potService.requestPostDiary(
            DiaryRequest(
                potId,
                null,
                water,
                null,
                null,
                null,
                nutrients
            ), null
        )
            .enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.code() == 200) {
                        var body = response.body()
                        Log.d(TAG, "$body")
                        if (body != null) {
                            Toast.makeText(context, "$info 가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d(TAG, "onResponse() 메인 체크 실패ㅜㅜㅜ $response")
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Log.d(TAG, "메인 체크 실패")
                }
            })
    }
}