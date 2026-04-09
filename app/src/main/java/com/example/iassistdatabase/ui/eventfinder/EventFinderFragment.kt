package com.example.iassistdatabase.ui.eventfinder

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.iassistdatabase.databinding.FragmentEventfinderBinding
import com.google.firebase.database.*
import com.example.iassistdatabase.R

class EventFinderFragment : Fragment() {

    private lateinit var binding: FragmentEventfinderBinding
    private lateinit var databaseRef: DatabaseReference
    private var eventItems: List<EventItem> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventfinderBinding.inflate(inflater, container, false)
        databaseRef = FirebaseDatabase.getInstance().getReference("events")

        setupRadioButtons()
        setupFilterButton()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            val selectedCategory = getSelectedCategory()
            if (selectedCategory.isNotEmpty()) {
                fetchEventData(selectedCategory)
            } else {
                Toast.makeText(context, "Select a category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRadioButtons() {
        val radioButtons = listOf(
            binding.radioTechnical,
            binding.radioSports,
            binding.radioLiterary,
            binding.radioCultural,
            binding.radioSociety,
            binding.radioOthers
        )

        for (button in radioButtons) {
            button.setOnClickListener {
                radioButtons.forEach { it.isChecked = false }
                button.isChecked = true
            }
        }
    }

    private fun getSelectedCategory(): String {
        val radioButtons = listOf(
            binding.radioTechnical to "technical",
            binding.radioSports to "sports",
            binding.radioLiterary to "literary",
            binding.radioCultural to "cultural",
            binding.radioSociety to "society",
            binding.radioOthers to "others"
        )
        for ((button, category) in radioButtons) {
            if (button.isChecked) return category
        }
        return ""
    }

    private fun fetchEventData(category: String) {
        databaseRef.child(category).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<EventItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(EventItem::class.java)
                    if (item != null) {
                        // Log the image URL for debugging
                        android.util.Log.d("EVENT_ITEM", "Image URL: ${item.imgurl}")
                        items.add(item)
                    }
                }
                if (items.isNotEmpty()) {
                    setupImageSlider(items)
                } else {
                    Toast.makeText(context, "No events found for $category", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupImageSlider(items: List<EventItem>) {
        eventItems = items
        binding.eventImageSlider.adapter = SliderAdapter(items)
        binding.eventImageSlider.visibility = View.VISIBLE
        binding.dotsLayout.visibility = View.VISIBLE
        binding.eventDescriptionText.visibility = View.VISIBLE
        setupDots(items.size)
        showDescription(0)

        binding.eventImageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                showDescription(position)
            }
        })
    }

    private fun setupDots(count: Int) {
        binding.dotsLayout.removeAllViews()
        for (i in 0 until count) {
            val dot = ImageView(requireContext()).apply {
                // Set the dot as active for the first one, inactive for others
                setImageResource(if (i == 0) R.drawable.active_dot else R.drawable.inactive_dot)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // same as in addDots
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0) // same margin spacing
                }
            }
            binding.dotsLayout.addView(dot)
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until binding.dotsLayout.childCount) {
            val dot = binding.dotsLayout.getChildAt(i) as ImageView
            dot.setImageResource(
                if (i == position) R.drawable.active_dot else R.drawable.inactive_dot
            )
        }
    }



    private fun showDescription(position: Int) {
        binding.eventDescriptionText.text = eventItems[position].desc
    }
}
