import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iassistdatabase.R

class ImageSliderMain(
    private val context: Context,
    private val imageUrls: List<String>

) : RecyclerView.Adapter<ImageSliderMain.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(url)
            .placeholder(R.drawable.placeholder) // shown while loading
            .error(R.drawable.error_placeholder) // shown if load fails
            .into(holder.imageView)

    }

    override fun getItemCount() = imageUrls.size
}
