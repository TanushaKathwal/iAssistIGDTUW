package com.example.iassistdatabase.ui.lifeinmotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.iassistdatabase.R
import com.google.firebase.database.*
import com.example.iassistdatabase.ui.lifeinmotion.LifeInMotionSliderAdapter

class LifeInMotionFragment : Fragment() {

    private lateinit var parentLayout: LinearLayout
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lifeinmotion, container, false)

        parentLayout = view.findViewById(R.id.parentLayout)
        database = FirebaseDatabase.getInstance().getReference("lifeinmotion")

        loadData()

        return view
    }

    private fun loadData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                parentLayout.removeAllViews()

                for (setSnapshot in snapshot.children) {
                    val images = mutableListOf<String>()
                    setSnapshot.child("images").children.forEach {
                        it.getValue(String::class.java)?.let { url -> images.add(url) }
                    }

                    val caption = setSnapshot.child("caption").getValue(String::class.java) ?: ""

                    if (images.isNotEmpty()) {
                        addSliderSet(images, caption)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle Firebase error here if needed
            }
        })
    }

    private fun addSliderSet(images: List<String>, caption: String) {
        val inflater = LayoutInflater.from(requireContext())
        val sliderSetView = inflater.inflate(R.layout.slider_set, parentLayout, false)

        val viewPager = sliderSetView.findViewById<ViewPager2>(R.id.imageSlider)
        val captionText = sliderSetView.findViewById<TextView>(R.id.captionText)
        val dotsLayout = sliderSetView.findViewById<LinearLayout>(R.id.dotsLayout)

        // Set caption
        captionText.text = caption

        // Attach adapter
        val adapter = LifeInMotionSliderAdapter(images)
        viewPager.adapter = adapter

        // Show dots immediately
        viewPager.post {
            setupDots(images.size, viewPager.currentItem, dotsLayout)
        }

        // Change dots on page change
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupDots(images.size, position, dotsLayout)
            }
        })

        // Add to layout
        parentLayout.addView(sliderSetView)
    }

    private fun setupDots(count: Int, selectedPosition: Int, dotsLayout: LinearLayout) {
        dotsLayout.removeAllViews()

        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(
                    if (i == selectedPosition) R.drawable.active_dot else R.drawable.inactive_dot
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
            }
            dotsLayout.addView(dot)
        }
    }
}