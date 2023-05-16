import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chocobi.groot.R
import java.io.File

class PostImageAdapter(private val imageList: ArrayList<File?>, private val context: Context) :
    RecyclerView.Adapter<PostImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)

        val view: View =
            inflater.inflate(R.layout.fragment_community_post_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = imageList[position]

        Log.d("PostImageAdapter-ImageList", imageUri.toString())
        val listener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                holder.galleryView.viewTreeObserver.removeOnPreDrawListener(this)
                val viewWidth = holder.galleryView.width
                val viewHeight = holder.galleryView.height
                Log.d("ImageView Size", "width: $viewWidth, height: $viewHeight")
                Glide.with(context)
                    .load(imageUri)
                    .override(viewWidth, viewHeight)
                    .centerCrop()
                    .into(holder.galleryView)
                return true
            }
        }
        holder.galleryView.viewTreeObserver.addOnPreDrawListener(listener)

        holder.galleryView.setOnClickListener {
            imageList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        Log.d("imageCount", imageList.size.toString())
        return imageList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val galleryView: ImageView = view.findViewById(R.id.galleryView)
    }
}
