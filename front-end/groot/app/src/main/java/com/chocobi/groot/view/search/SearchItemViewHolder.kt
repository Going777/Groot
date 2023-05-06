package com.chocobi.groot.view.search

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chocobi.groot.R
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.search.model.PlantSearchResponse
import java.lang.ref.WeakReference

class SearchItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        interface ItemViewHolderDelegate {
            fun onItemViewClick(plantSearchResponse: PlantSearchResponse) {
                Log.d("ItemViewHolderDelegate", "onItemViewClick()")
            }
        }

    private var view: WeakReference<View> = WeakReference(itemView)

    private lateinit var searchPlantImgView: ImageView
    private lateinit var searchPlantNameTextView: TextView

    var delegate: ItemViewHolderDelegate? = null
    lateinit var plantSearchResponse: PlantSearchResponse

    init {
        findView()
        setListener()
    }

    private fun findView() {
        view.get()?.let {
            searchPlantImgView = it.findViewById(R.id.dictRvItemImage)
            searchPlantNameTextView = it.findViewById(R.id.dictRvItemName)
        }
    }

    private fun setListener() {
        view.get()?.setOnClickListener {
            delegate?.onItemViewClick(plantSearchResponse)
        }
    }

    fun updateView() {
        val plantImg = plantSearchResponse.plants[0].img.toString()
        val plantName = plantSearchResponse.plants[0].krName
        GlobalVariables.changeImgView(searchPlantImgView, plantImg, itemView.context)
        searchPlantNameTextView.text = plantSearchResponse.plants[0].krName
    }
}