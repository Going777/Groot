package com.chocobi.groot.view.user.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.main.Character
import java.lang.ref.WeakReference

class CollectionRVAdapter(
    private val items: MutableList<Character>,
    private var positions: MutableList<Int>

) : RecyclerView.Adapter<CollectionRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_collection_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    interface ItemClick {
        fun onClick(view: View, position: Int, grwType: String, level: Int, isContain: Boolean)
    }

    var itemClick: ItemClick? = null

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isContain = isContain(position)
        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position, items[position].grwType, items[position].level, isContain)
            }
        }
        holder.bindItems(items[position], position, positions)
    }

    fun isContain(position: Int): Boolean {
        if (position in positions) {
            return true
        }
        return false
    }

    // positions 업데이트 메서드 추가
    fun updatePositions(newPositions: MutableList<Int>) {
        positions = newPositions
    }

    inner class ViewHolder(itemView: View, private val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        private var view: WeakReference<View> = WeakReference(itemView)
        private val grwTypeText: TextView = itemView.findViewById(R.id.grwTypeText)
        private val levelText: TextView = itemView.findViewById(R.id.levelText)

        fun bindItems(item: Character, position: Int, positions: MutableList<Int>) {
            Log.d("ViewHolder", "bindItems() $positions")
            val characterImage: ImageView = itemView.findViewById(R.id.characterImage)
            val level = item.level
//            val progressBar: ProgressBar = itemView.findViewById(R.id.loadingProgressBar)

            // 이미지 로딩 전에 ProgressBar 표시

            val layoutParams = characterImage.layoutParams as ViewGroup.LayoutParams
            if (level == 0) {
                layoutParams.width = 160
                layoutParams.height = 160
            } else if (level == 1) {
                layoutParams.width = 240
                layoutParams.height = 240
            } else {
                layoutParams.width = 300
                layoutParams.height = 300
            }
            characterImage.layoutParams = layoutParams

            characterImage.setImageDrawable(null)

            if (position in positions) {
                GlobalVariables.changeImgView(characterImage, item.pngPath, context)
                // 이미지 로딩이 완료되면 ProgressBar 감추기
//                progressBar.visibility = View.GONE
            } else {
                GlobalVariables.changeImgView(characterImage, item.greyPath, context)
            }

            grwTypeText.text = item.grwType

            var levelString = ""
            if (item.level == 0) {
                levelString = "Lv 1~4"
            } else if (item.level == 1) {
                levelString = "Lv 5~9"
            } else {
                levelString = "Lv 10~"
            }
            levelText.text = levelString
        }
    }
}

