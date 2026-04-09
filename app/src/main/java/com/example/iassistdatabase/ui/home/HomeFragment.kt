package com.example.iassistdatabase.ui.home

import ImageSliderMain
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentHomeBinding
import com.example.iassistdatabase.GridPagerAdapter
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageSlider: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var imageUrls: MutableList<String>

    private var currentImagePage = 0

    // Grid ViewPager vars
    private lateinit var gridPagerAdapter: GridPagerAdapter
    private var currentGridPage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        imageSlider = binding.imageSlider
        dotsLayout = binding.dotsLayout
        imageUrls = mutableListOf()

        fetchSliderImagesFromFirebase()

        // Setup grid ViewPager (unrelated to slider)
        gridPagerAdapter = GridPagerAdapter(this)
        binding.viewPager.adapter = gridPagerAdapter
        setupGridDots(gridPagerAdapter.itemCount)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateGridDots(position)
                currentGridPage = position
            }
        })

        return view
    }

    private fun fetchSliderImagesFromFirebase() {
        val database = FirebaseDatabase.getInstance()
        val imagesRef = database.getReference("homesliderimages")

        imagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val url = child.child("imageurl").getValue(String::class.java)
                    if (url != null) {
                        Log.d("FirebaseImage", "Fetched URL: $url")
                        imageUrls.add(url)
                    }
                }

                if (!isAdded || view == null) return // prevent crash if fragment is detached

                if (imageUrls.isNotEmpty()) {
                    context?.let { safeContext ->
                        imageSlider.adapter = ImageSliderMain(safeContext, imageUrls)
                        addDots(imageUrls.size)
                        setCurrentDot(0)

                        imageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                setCurrentDot(position)
                                currentImagePage = position
                            }
                        })
                    }
                } else {
                    Log.d("FirebaseImage", "No image URLs found in sliderImages node.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseImage", "Database error: ${error.message}")
            }
        })
    }



    // IMAGE SLIDER dot handling
    private fun addDots(count: Int) {
        dotsLayout.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(R.drawable.inactive_dot)
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

    private fun setCurrentDot(index: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val dot = dotsLayout.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == index) R.drawable.active_dot else R.drawable.inactive_dot
            )
        }
    }

    // GRID slider dot handling (already present)
    private fun setupGridDots(count: Int) {
        binding.dotsContainer.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                setImageResource(if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
            }
            binding.dotsContainer.addView(dot)
        }
    }

    private fun updateGridDots(index: Int) {
        for (i in 0 until binding.dotsContainer.childCount) {
            val dot = binding.dotsContainer.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == index) R.drawable.active_dot else R.drawable.inactive_dot
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
