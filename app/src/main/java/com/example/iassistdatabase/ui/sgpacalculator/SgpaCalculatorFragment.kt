package com.example.iassistdatabase.ui.sgpacalculator

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.iassistdatabase.R
import com.example.iassistdatabase.utils.SGPADataUploader
import com.google.firebase.firestore.FirebaseFirestore

class SgpaCalculatorFragment : Fragment() {

    private lateinit var branchSpinner: Spinner
    private lateinit var semesterSpinner: Spinner
    private lateinit var subjectsLayout: LinearLayout
    private lateinit var sgpaResultText: TextView
    private lateinit var calculateButton: Button

    private val gradeSpinners = mutableListOf<Spinner>()
    private val subjectNames = mutableListOf<String>()

    private val db = FirebaseFirestore.getInstance()
    private val gradePoints = mutableMapOf<String, Int>()
    private val subjectCredits = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sgpacalculator, container, false)

        branchSpinner = view.findViewById(R.id.branch_spinner)
        semesterSpinner = view.findViewById(R.id.semester_spinner)
        subjectsLayout = view.findViewById(R.id.subjects_layout)
        sgpaResultText = view.findViewById(R.id.sgpa_result)
        calculateButton = view.findViewById(R.id.calculate_button)

        setupSpinners()
        fetchGradePoints()

        calculateButton.setOnClickListener {
            if (gradeSpinners.isEmpty() || subjectNames.isEmpty()) {
                Toast.makeText(requireContext(), "Please select department and semester first.", Toast.LENGTH_SHORT).show()
            } else {
                calculateSGPA()
            }
        }

        SGPADataUploader.uploadSGPAData() // optional feature

        return view
    }

    private fun setupSpinners() {
        val branches = listOf("Department", "CSE", "CSE-AI", "ECE", "ECE-AI")
        val semesters = listOf("Semester", "Sem1", "Sem2")

        branchSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, branches)
        semesterSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, semesters)

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedBranch = branchSpinner.selectedItem?.toString() ?: return
                val selectedSemester = semesterSpinner.selectedItem?.toString() ?: return

                Log.d("DEBUG_SGPA", "Selected Branch: $selectedBranch, Semester: $selectedSemester")

                if (selectedBranch != "Department" && selectedSemester != "Semester") {
                    loadSubjects(selectedBranch, selectedSemester)
                } else {
                    subjectsLayout.removeAllViews()
                    gradeSpinners.clear()
                    subjectNames.clear()
                    sgpaResultText.text = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        branchSpinner.onItemSelectedListener = listener
        semesterSpinner.onItemSelectedListener = listener
    }

    private fun fetchGradePoints() {
        db.collection("grades").document("gradePoints").get()
            .addOnSuccessListener { document ->
                document.data?.forEach { (grade, point) ->
                    gradePoints[grade] = point.toString().toInt()
                }
            }
    }

    private fun loadSubjects(branch: String, semester: String) {

        Log.d("DEBUG_SGPA", "Branch: $branch, Semester: $semester")

        // Check if default values are selected
        if (branch == "Department" || semester == "Semester") {
            Toast.makeText(requireContext(), "Please select a valid branch and semester", Toast.LENGTH_SHORT).show()
            return
        }

        val key = "${branch}_${semester}"
        Log.d("DEBUG_SGPA", "Fetching Firestore document for key: $key")

        db.collection("courses").document(key).get()
            .addOnSuccessListener { document ->
                Log.d("DEBUG_SGPA", "Document exists: ${document.exists()}")

                val subjectList = document.get("subjects") as? List<HashMap<String, Any>>
                if (subjectList == null) {
                    Log.d("DEBUG_SGPA", "No subject list found or incorrect format")
                    return@addOnSuccessListener
                }

                Log.d("DEBUG_SGPA", "Subjects retrieved: ${subjectList.size}")

                subjectsLayout.removeAllViews()
                gradeSpinners.clear()
                subjectNames.clear()

                subjectList.forEachIndexed { index, subject ->
                    val name = subject["name"] as? String
                    val credits = (subject["credits"] as? Long)?.toInt()

                    if (name == null || credits == null) {
                        Log.d("DEBUG_SGPA", "Skipping subject at index $index due to missing name or credits")
                        return@forEachIndexed
                    }

                    Log.d("DEBUG_SGPA", "Adding subject: $name with $credits credits")

                    subjectCredits[name] = credits
                    subjectNames.add(name)

                    val row = layoutInflater.inflate(R.layout.subject_grade_row, subjectsLayout, false)
                    val subjectNameText = row.findViewById<TextView>(R.id.subject_name)
                    val gradeSpinner = row.findViewById<Spinner>(R.id.grade_spinner)

                    subjectNameText.text = name
                    val gradeList = listOf("A+", "A", "B+", "B", "C", "D", "F")
                    val gradeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, gradeList)
                    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    gradeSpinner.adapter = gradeAdapter


                    subjectsLayout.addView(row)
                    gradeSpinners.add(gradeSpinner)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DEBUG_SGPA", "Error fetching subjects: ${exception.message}")
            }
    }


    private fun calculateSGPA() {
        var totalCredits = 0
        var totalPoints = 0.0

        for (i in gradeSpinners.indices) {
            val grade = gradeSpinners[i].selectedItem?.toString()
            val subject = subjectNames[i]

            if (grade.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Select all grades", Toast.LENGTH_SHORT).show()
                return
            }

            val credits = subjectCredits[subject] ?: 0
            val point = gradePoints[grade] ?: 0

            totalCredits += credits
            totalPoints += credits * point
        }

        val sgpa = if (totalCredits > 0) totalPoints / totalCredits else 0.0
        val message = getSGPAMessage(sgpa)
        sgpaResultText.text = "Your SGPA is %.2f\n$message".format(sgpa)
    }

    private fun getSGPAMessage(sgpa: Double): String {
        return when {
            sgpa >= 9.5 -> "Exceptional achievement!"
            sgpa in 9.0..9.49 -> "Amazing work! Stay Sharp!"
            sgpa in 8.5..8.99 -> "Impressive! Keep Going!"
            sgpa in 8.0..8.49 -> "Great work! Aim Higher!"
            sgpa in 7.5..7.99 -> "You're on track! Stay Consistent!"
            sgpa in 7.0..7.49 -> "Good effort! Stay determined!"
            sgpa in 6.5..6.99 -> "You're improving! Believe!"
            sgpa in 6.0..6.49 -> "Room to grow! Keep pushing forward!"
            else -> "Keep trying! Never give up!"
        }
    }
}
