package com.example.iassistdatabase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class GridFragment : Fragment() {

    companion object {
        fun newInstance(imageIds: List<Int>): GridFragment {
            val fragment = GridFragment()
            val args = Bundle()
            args.putIntegerArrayList("images", ArrayList(imageIds))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.grid_page, container, false) as GridLayout
        val images = arguments?.getIntegerArrayList("images") ?: arrayListOf()

        images.forEach { resId ->
            val imageView = ImageView(context).apply {
                setImageResource(resId)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(2, 2, 2, 2)
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setOnClickListener {
                    Toast.makeText(context, "Clicked on item $resId", Toast.LENGTH_SHORT).show()
                }
            }
            root.addView(imageView)
        }

        return root
    }
}