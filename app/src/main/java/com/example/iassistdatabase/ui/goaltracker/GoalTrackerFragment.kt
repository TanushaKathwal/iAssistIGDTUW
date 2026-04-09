package com.example.iassistdatabase.ui.goaltracker

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GoalTrackerFragment : Fragment() {

    private lateinit var departmentSpinner: Spinner
    private lateinit var semesterSpinner: Spinner
    private lateinit var subjectLayout: LinearLayout
    private lateinit var pieChart: PieChart

    private val subjectProgress = mutableMapOf<String, Int>()
    private val subjects = mutableListOf<String>()

    private val departments = listOf("Department", "CSE", "CSE AI", "ECE", "ECE AI", "AIML", "MAE", "IT")
    private val semesters = listOf("Semester") + (1..8).map { it.toString() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please login to access Goal Tracker", Toast.LENGTH_SHORT).show()

            val placeholderView = FrameLayout(requireContext())
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_goalTrackerFragment_to_loginFragment)
            }, 1000)

            return placeholderView
        }

        val view = inflater.inflate(R.layout.fragment_goaltracker, container, false)

        departmentSpinner = view.findViewById(R.id.departmentSpinner)
        semesterSpinner = view.findViewById(R.id.semesterSpinner)
        subjectLayout = view.findViewById(R.id.subjectLayout)
        pieChart = view.findViewById(R.id.pieChart)

        setupSpinners()
        return view
    }

    private fun setupSpinners() {
        departmentSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, departments)
        semesterSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, semesters)

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDept = departmentSpinner.selectedItem.toString()
                val selectedSem = semesterSpinner.selectedItem.toString()

                if (selectedDept != "Department" && selectedSem != "Semester") {
                    subjectLayout.visibility = View.VISIBLE
                    fetchSubjectsFromFirebase(selectedDept, selectedSem)
                } else {
                    subjectLayout.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        departmentSpinner.onItemSelectedListener = listener
        semesterSpinner.onItemSelectedListener = listener

        subjectLayout.visibility = View.GONE
    }

    private fun fetchSubjectsFromFirebase(department: String, semester: String) {
        val dbRef = FirebaseDatabase.getInstance()
            .getReference("subjects/${department.lowercase()}/sem$semester")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                subjects.clear()
                subjectProgress.clear()
                subjectLayout.removeAllViews()

                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "No subjects found", Toast.LENGTH_SHORT).show()
                    updatePieChart()
                    return
                }

                for (child in snapshot.children) {
                    val subject = child.getValue(String::class.java)
                    subject?.let {
                        subjects.add(it)
                        subjectProgress[it] = 0
                        addSubjectView(it)
                    }
                }

                updatePieChart()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addSubjectView(subject: String) {
        val view = layoutInflater.inflate(R.layout.item_subjectprogress, subjectLayout, false)
        val subjectName = view.findViewById<TextView>(R.id.subjectName)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        subjectName.text = subject
        seekBar.progress = 0

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                subjectProgress[subject] = progress
                updatePieChart()
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        subjectLayout.addView(view)
    }

    private fun updatePieChart() {
        val totalSubjects = subjectProgress.size
        if (totalSubjects == 0) {
            pieChart.clear()
            pieChart.invalidate()
            return
        }

        val averageCovered = subjectProgress.values.sum() / totalSubjects.toFloat()
        val covered = averageCovered
        val uncovered = 100 - averageCovered

        val entries = listOf(
            PieEntry(covered, "Complete"),
            PieEntry(uncovered, "Incomplete")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 16f

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%"
            }
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Syllabus Progress"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.invalidate()
    }
}