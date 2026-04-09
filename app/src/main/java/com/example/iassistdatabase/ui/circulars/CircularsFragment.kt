package com.example.iassistdatabase.ui.circulars

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iassistdatabase.databinding.FragmentCircularsBinding
import com.example.iassistdatabase.model.Circular
import com.google.firebase.database.*

class CircularsFragment : Fragment() {

    private var _binding: FragmentCircularsBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var circularsList: List<Circular> = emptyList()
    private lateinit var adapter: CircularsAdapter
    private lateinit var yearSpinner: Spinner
    private lateinit var monthSpinner: Spinner
    private lateinit var searchButton: Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCircularsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Define monthMap to convert month names to numbers
        val monthMap = mapOf(
            "January" to "01",
            "February" to "02",
            "March" to "03",
            "April" to "04",
            "May" to "05",
            "June" to "06",
            "July" to "07",
            "August" to "08",
            "September" to "09",
            "October" to "10",
            "November" to "11",
            "December" to "12"
        )

        circularsList = ArrayList()

        adapter = CircularsAdapter(circularsList) { url ->
            openPdf(url)
        }

        binding.circularsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.circularsRecyclerView.adapter = adapter

        binding.circularsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Step 1: Initialize Spinner & Button
        val yearSpinner = binding.yearSpinner
        val monthSpinner = binding.monthSpinner
        val searchButton = binding.searchButton

        // Step 2: Populate the spinners
        val years = listOf("Year","2023", "2024", "2025")
        val months = listOf("Month",
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        yearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        monthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)

        // Search Button Click
        searchButton.setOnClickListener {
            val selectedYear = yearSpinner.selectedItem.toString()
            val selectedMonthName = monthSpinner.selectedItem.toString()

            // Convert month name to "MM" format using the monthMap
            val selectedMonth = monthMap[selectedMonthName] ?: ""

            val filteredList = circularsList.filter { circular ->
                circular.date?.let { date ->
                    val parts = date.split("-") // Assuming format is yyyy-MM-dd
                    val year = parts[0]
                    val month = parts[1]
                    year == selectedYear && month == selectedMonth
                } ?: false
            }

            adapter.updateList(filteredList)
        }

        database = FirebaseDatabase.getInstance().getReference("circulars")

        fetchCircularsFromFirebase()
    }

    private fun fetchCircularsFromFirebase() {

        // Show progress bar
        binding.progressBar.visibility = View.VISIBLE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                circularsList = snapshot.children.mapNotNull { it.getValue(Circular::class.java) }
                    .sortedByDescending { it.date }

                adapter.updateList(circularsList)

                // ✅ Hide progress bar
                binding.progressBar.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load circulars", Toast.LENGTH_SHORT).show()

                // ✅ Hide progress bar even on error
                binding.progressBar.visibility = View.GONE

            }
        })
    }



    private fun openPdf(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No browser found to open PDF", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
