package com.example.iassistdatabase.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object SGPADataUploader {

    fun uploadSGPAData() {
        val db = FirebaseFirestore.getInstance()

        val courseMap = mapOf(
            "CSE_Sem1" to listOf(
                "Applied Physics" to 4, "Applied Maths" to 4, "Programming with C" to 4,
                "Web Application Dev." to 3, "Communication Skills" to 3, "BEE" to 3
            ),
            "CSE_Sem2" to listOf(
                "Environmental Science" to 4, "Probability & Statistics" to 4, "Data Structures" to 4,
                "Soft Skills & Personality Dev." to 3, "Mobile Application Dev." to 3,
                "Intro to Data Science" to 3
            ),
            "ECE_Sem1" to listOf(
                "Probability & Statistics" to 4, "Environmental Science" to 4, "Signals and Systems" to 3,
                "Programming with Python" to 4, "Communication Skills" to 3, "Applied Maths" to 4
            ),
            "ECE_Sem2" to listOf(
                "Applied Maths" to 4, "Physics" to 4, "Data Structures" to 4, "IT Workshop" to 3,
                "Network Analysis" to 3, "Soft Skills & Personality Dev." to 3
            ),
            "CSE-AI_Sem1" to listOf(
                "Environmental Science" to 4, "Probability & Statistics" to 4,
                "Communication Skills" to 3, "Programming with Python" to 4,
                "BEE" to 3, "IT Workshop" to 3
            ),
            "CSE-AI_Sem2" to listOf(
                "Applied Maths" to 4, "Applied Physics" to 4, "Soft Skills & Personality Dev." to 3,
                "Web Application Dev." to 3, "Data Structures" to 4, "Intro to Data Science" to 3
            ),
            "ECE-AI_Sem1" to listOf(
                "Environmental Science" to 4, "Probability & Statistics" to 4,
                "Communication Skills" to 3, "Analog Electronics" to 3,
                "Signals and Systems" to 3, "Programming with Python" to 4
            ),
            "ECE-AI_Sem2" to listOf(
                "Applied Maths" to 4, "Applied Physics" to 4,
                "Soft Skills & Personality Dev." to 3, "IT Workshop" to 3,
                "Data Structures" to 4, "Network Analysis" to 3
            )
        )

        for ((courseId, subjects) in courseMap) {
            val subjectsList = subjects.map { (name, credits) ->
                mapOf("name" to name, "credits" to credits)
            }

            val data = hashMapOf("subjects" to subjectsList)

            db.collection("courses").document(courseId)
                .set(data)
                .addOnSuccessListener {
                    Log.d("SGPADataUpload", "Successfully uploaded $courseId")
                }
                .addOnFailureListener { e ->
                    Log.e("SGPADataUpload", "Error uploading $courseId: ${e.message}")
                }
        }

        // Upload grade-to-point mapping
        val gradePointsMap = hashMapOf(
            "A+" to 10, "A" to 9, "B+" to 8, "B" to 7,
            "C" to 6, "D" to 5, "F" to 0
        )

        db.collection("grades").document("gradePoints")
            .set(gradePointsMap)
            .addOnSuccessListener {
                Log.d("SGPADataUpload", "Grade points uploaded")
            }
            .addOnFailureListener { e ->
                Log.e("SGPADataUpload", "Failed to upload grade points: ${e.message}")
            }
    }
}
