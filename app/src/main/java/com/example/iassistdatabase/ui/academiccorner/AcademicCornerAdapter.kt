package com.example.iassistdatabase.ui.academiccorner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iassistdatabase.R
import com.example.iassistdatabase.model.Datesheet

class AcademicCornerAdapter(
    private var itemList: List<Datesheet>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AcademicCornerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.circular_number)
        private val titleTextView: TextView = itemView.findViewById(R.id.circular_title)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val url = itemList[position].url
                    onItemClick(url)
                }
            }
        }

        fun bind(item: Datesheet, position: Int) {
            numberTextView.text = "${position + 1}"
            titleTextView.text = item.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_circular, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position], position)
    }

    fun updateList(newList: List<Datesheet>) {
        itemList = newList
        notifyDataSetChanged()
    }
}
