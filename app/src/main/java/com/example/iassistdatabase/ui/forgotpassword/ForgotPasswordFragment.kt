package com.example.iassistdatabase.ui.forgotpassword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.google.firebase.database.FirebaseDatabase

class ForgotPasswordFragment : Fragment() {

    private lateinit var emailInput: EditText
    private lateinit var checkEmailButton: Button
    private lateinit var question1Text: TextView
    private lateinit var answer1Input: EditText
    private lateinit var question2Text: TextView
    private lateinit var answer2Input: EditText
    private lateinit var verifyButton: Button
    private lateinit var questionsLayout: LinearLayout

    private val databaseRef = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_forgotpassword, container, false)

        // UI binding
        emailInput = view.findViewById(R.id.emailInput)
        checkEmailButton = view.findViewById(R.id.verifyEmailButton)
        question1Text = view.findViewById(R.id.securityQuestion1)
        answer1Input = view.findViewById(R.id.answer1Input)
        question2Text = view.findViewById(R.id.securityQuestion2)
        answer2Input = view.findViewById(R.id.answer2Input)
        verifyButton = view.findViewById(R.id.verifyButton)
        questionsLayout = view.findViewById(R.id.securitySection)

        questionsLayout.visibility = View.GONE
        verifyButton.visibility = View.GONE

        checkEmailButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            databaseRef.orderByChild("email").equalTo(email).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userSnapshot = snapshot.children.first()

                    val q1 = userSnapshot.child("question1").value?.toString()
                    val a1 = userSnapshot.child("answer1").value?.toString()
                    val q2 = userSnapshot.child("question2").value?.toString()
                    val a2 = userSnapshot.child("answer2").value?.toString()

                    if (!q1.isNullOrEmpty() && !q2.isNullOrEmpty()) {
                        question1Text.text = q1
                        question2Text.text = q2

                        questionsLayout.visibility = View.VISIBLE
                        verifyButton.visibility = View.VISIBLE

                        verifyButton.setOnClickListener {
                            val userAnswer1 = answer1Input.text.toString().trim()
                            val userAnswer2 = answer2Input.text.toString().trim()

                            if (userAnswer1.equals(a1, ignoreCase = true) &&
                                userAnswer2.equals(a2, ignoreCase = true)
                            ) {
                                com.google.firebase.auth.FirebaseAuth.getInstance()
                                    .sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(requireContext(), "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show()
                                            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                                        } else {
                                            Toast.makeText(requireContext(), "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                            } else {
                                Toast.makeText(requireContext(), "Incorrect answers", Toast.LENGTH_SHORT).show()
                            }
                        }

                        Toast.makeText(requireContext(), "Email verified. Answer the questions.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Security questions not found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Email not found.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to access database: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ForgotPassword", "Database error", error)
            }
        }
        return view
    }
}
