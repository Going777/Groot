package com.chocobi.groot.view.user.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.AsyncTask
import android.provider.Settings.Global
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.chocobi.groot.R
import com.chocobi.groot.Thread.ThreadUtil
import com.chocobi.groot.data.GlobalVariables
import com.chocobi.groot.view.main.Character
import java.lang.ref.WeakReference
import java.net.URL

class CollectionRVAdapter(
    private val items: MutableList<Character>,
    private val positions: MutableList<Int>

) : RecyclerView.Adapter<CollectionRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_collection_item, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position], position, positions)
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
            } else {
                GlobalVariables.changeImgView(characterImage, item.greyPath, context)
//                val downloadTask = DownloadAndConvertImageTask(characterImage, progressBar)
//                downloadTask.execute(item.pngPath)
                // 이미지 로딩이 완료되면 ProgressBar 감추기
//                progressBar.visibility = View.GONE
            }

            grwTypeText.text = item.grwType

            var levelString = ""
            if(item.level == 0) {
                levelString = "Lv 1~4"
            } else if(item.level == 1) {
                levelString = "Lv 5~9"
            } else {
                levelString = "Lv 10~"
            }
            levelText.text = levelString
        }
    }

//    private inner class DownloadAndConvertImageTask(private val imageView: ImageView, private val progressBar: ProgressBar) :
//        AsyncTask<String, Void, Bitmap>() {
//        override fun doInBackground(vararg params: String?): Bitmap? {
//            val imageUrl = params[0]
//            return if (imageUrl != null) {
//                val inputStream = URL(imageUrl).openStream()
//                val originalBitmap = BitmapFactory.decodeStream(inputStream)
//                inputStream.close()
//
//                convertToDarkGrayWithOpacity(originalBitmap)
//            } else {
//                null
//            }
//        }
//
//        override fun onPostExecute(result: Bitmap?) {
//            if (result != null) {
//                imageView.setImageBitmap(result)
//                // 이미지 로딩이 완료되면 ProgressBar 감추기
//                progressBar.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun convertToDarkGrayWithOpacity(originalBitmap: Bitmap): Bitmap {
//        val darkGrayBitmap = Bitmap.createBitmap(
//            originalBitmap.width,
//            originalBitmap.height,
//            Bitmap.Config.ARGB_8888
//        )
//
//        val canvas = Canvas(darkGrayBitmap)
//
//        val paint = Paint().apply {
//            colorFilter = ColorMatrixColorFilter(
//                ColorMatrix().apply {
//                    setSaturation(0f)
//                    setScale(0.2f, 0.2f, 0.2f, 1f)
//                }
//            )
//        }
//
//        canvas.drawBitmap(originalBitmap, 0f, 0f, paint)
//
//        for (y in 0 until darkGrayBitmap.height) {
//            for (x in 0 until darkGrayBitmap.width) {
//                val pixel = darkGrayBitmap.getPixel(x, y)
//                val alpha = Color.alpha(pixel)
//                val darkGrayPixel = Color.argb(alpha, 0x33, 0x33, 0x33)
//                darkGrayBitmap.setPixel(x, y, darkGrayPixel)
//            }
//        }
//
//        return darkGrayBitmap
//    }
}

