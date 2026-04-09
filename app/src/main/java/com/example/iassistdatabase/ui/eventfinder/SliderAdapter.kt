package com.example.iassistdatabase.ui.eventfinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.iassistdatabase.databinding.ItemSliderBinding

class SliderAdapter(private val items: List<EventItem>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(private val binding: ItemSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventItem) {
            Glide.with(binding.root.context)
                .load(item.imgurl)
                .into(binding.sliderImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
