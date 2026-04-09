package com.example.iassistdatabase.ui.academiccorner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iassistdatabase.databinding.FragmentAcademiccornerBinding
import com.example.iassistdatabase.model.Timetables
import com.example.iassistdatabase.model.Datesheet
import com.google.firebase.database.*

class AcademicCornerFragment : Fragment() {

    private var _binding: FragmentAcademiccornerBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: AcademicCornerAdapter

    private var timetablesList: List<Timetables> = emptyList()
    private var datesheetList: List<Datesheet> = emptyList()
    private var selectedType: String = "timetable"
    private var selectedBranch: String = ""

    private val monthMap = mapOf(
        "January" to "01", "February" to "02", "March" to "03",
        "April" to "04", "May" to "05", "June" to "06",
        "July" to "07", "August" to "08", "September" to "09",
        "October" to "10", "November" to "11", "December" to "12"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAcademiccornerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.GONE
        setupRecyclerView()
        setupSpinners()
        setupListeners()

        adapter.updateList(emptyList())
        hideAllFilters()

        // Simulate the selected radio button on load
        handleRadioSelection(binding.radioGroup.checkedRadioButtonId)
    }

    private fun setupSpinners() {
        val branches = listOf("Select Branch", "CSE", "CSE(AI)", "IT", "AIML", "ECE", "ECE(AI)", "MAE")
        val years = listOf("Year", "2023", "2024", "2025")
        val months = listOf("Month") + monthMap.keys

        binding.branchSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, branches)
        binding.yearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)
        binding.monthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)

        binding.branchSpinner.setSelection(0)
        binding.yearSpinner.setSelection(0)
        binding.monthSpinner.setSelection(0)

        binding.branchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!binding.radioTimetables.isChecked || position == 0) return
                else {
                    selectedBranch = branches[position].lowercase().replace("(", "").replace(")", "").replace(" ", "")
                    binding.yearSpinner.visibility = View.VISIBLE
                    binding.monthSpinner.visibility = View.VISIBLE
                    binding.searchButton.visibility = View.VISIBLE
                    binding.academiccornerRecyclerView.visibility = View.VISIBLE
                    fetchTimetablesFromFirebase(selectedBranch)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRecyclerView() {
        adapter = AcademicCornerAdapter(emptyList()) { url -> openPdf(url) }
        binding.academiccornerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.academiccornerRecyclerView.adapter = adapter
        binding.academiccornerRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    private fun setupListeners() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            handleRadioSelection(checkedId)
        }

        binding.searchButton.setOnClickListener {
            val selectedYear = binding.yearSpinner.selectedItem.toString()
            val selectedMonthName = binding.monthSpinner.selectedItem.toString()
            val selectedMonth = monthMap[selectedMonthName] ?: ""

            val filteredList = when (selectedType) {
                "timetable" -> timetablesList.filter {
                    val parts = it.date.split("-")
                    parts[0] == selectedYear && parts[1] == selectedMonth
                }.map { it.toCommonModel() }

                "datesheet" -> datesheetList.filter {
                    val parts = it.date.split("-")
                    parts[0] == selectedYear && parts[1] == selectedMonth
                }.map { it.toCommonModel() }

                else -> emptyList()
            }

            adapter.updateList(filteredList)
        }
    }

    private fun handleRadioSelection(checkedId: Int) {
        when (checkedId) {
            binding.radioTimetables.id -> {
                selectedType = "timetable"
                binding.branchSpinner.visibility = View.VISIBLE
                binding.branchSpinner.setSelection(0) // This triggers the listener again
            }

            binding.radioDatesheets.id -> {
                selectedType = "datesheet"

                binding.branchSpinner.visibility = View.GONE
                binding.yearSpinner.visibility = View.VISIBLE
                binding.monthSpinner.visibility = View.VISIBLE
                binding.searchButton.visibility = View.VISIBLE
                binding.academiccornerRecyclerView.visibility = View.VISIBLE

                fetchDatesheetsFromFirebase()
            }
        }
    }

    private fun fetchTimetablesFromFirebase(branch: String) {
        binding.progressBar.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("timetables/$branch")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timetablesList = snapshot.children.mapNotNull { it.getValue(Timetables::class.java) }
                    .sortedByDescending { it.date }

                adapter.updateList(timetablesList.map { it.toCommonModel() })
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load timetables", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchDatesheetsFromFirebase() {
        binding.progressBar.visibility = View.VISIBLE
        database = FirebaseDatabase.getInstance().getReference("datesheets")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                datesheetList = snapshot.children.mapNotNull { it.getValue(Datesheet::class.java) }
                    .sortedByDescending { it.date }

                adapter.updateList(datesheetList.map { it.toCommonModel() })
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load datesheets", Toast.LENGTH_SHORT).show()
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

    private fun hideAllFilters() {
        binding.branchSpinner.visibility = View.GONE
        binding.yearSpinner.visibility = View.GONE
        binding.monthSpinner.visibility = View.GONE
        binding.searchButton.visibility = View.GONE
        binding.academiccornerRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Timetables.toCommonModel() = Datesheet(title, date, url)
    private fun Datesheet.toCommonModel() = Datesheet(title, date, url)
}
