package com.example.iassistdatabase.ui.lifeinmotion

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LifeInMotionSliderAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<LifeInMotionSliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        return SliderViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(images[position])
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = images.size
}
