package com.example.iassistdatabase.ui.circulars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iassistdatabase.R
import com.example.iassistdatabase.model.Circular

class CircularsAdapter(
    private var circularList: List<Circular>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CircularsAdapter.CircularViewHolder>() {

    inner class CircularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTextView: TextView = itemView.findViewById(R.id.circular_number)
        val titleTextView: TextView = itemView.findViewById(R.id.circular_title)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = circularList[position].url
                    onItemClick(url)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circular, parent, false)
        return CircularViewHolder(view)
    }

    override fun onBindViewHolder(holder: CircularViewHolder, position: Int) {
        val circular = circularList[position]
        holder.numberTextView.text = "${position + 1}"
        holder.titleTextView.text = circular.title
    }

    override fun getItemCount() = circularList.size

    fun updateList(newList: List<Circular>) {
        circularList = newList
        notifyDataSetChanged()
    }

}
