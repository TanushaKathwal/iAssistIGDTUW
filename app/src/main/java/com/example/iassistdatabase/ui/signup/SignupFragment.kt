package com.example.iassistdatabase.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val questions1 = listOf(
        "Choose Security Question 1",
        "Your first pet's name?",
        "Your birthplace?",
        "Your favorite food?",
        "Your childhood nickname?",
        "Your dream job?"
    )

    private val questions2 = listOf(
        "Choose Security Question 2",
        "Name of your first school?",
        "Your favorite teacher?",
        "Your favorite book?",
        "Your favorite holiday destination?",
        "Your sibling's name?"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupSpinners()

        binding.signupButton.setOnClickListener {
            performSignup()
        }
    }

    private fun setupSpinners() {
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, questions1)
        val adapter2 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, questions2)

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerQuestion1.adapter = adapter1
        binding.spinnerQuestion2.adapter = adapter2
    }

    private fun performSignup() {
        val name = binding.editName.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString()
        val repassword = binding.editRePassword.text.toString()
        val question1 = binding.spinnerQuestion1.selectedItem.toString()
        val answer1 = binding.editAnswer1.text.toString().trim()
        val question2 = binding.spinnerQuestion2.selectedItem.toString()
        val answer2 = binding.editAnswer2.text.toString().trim()

        if (password != repassword) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return
        }

        if (name.isBlank() || email.isBlank() || password.isBlank()
            || answer1.isBlank() || answer2.isBlank()
            || question1 == questions1[0] || question2 == questions2[0]
        ) {
            Toast.makeText(context, "Please fill all fields properly", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userMap = mapOf(
                    "name" to name,
                    "email" to email,
                    "question1" to question1,
                    "answer1" to answer1,
                    "question2" to question2,
                    "answer2" to answer2
                )

                database.child("users").child(uid).setValue(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Signup Successful. Please login now.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // ✅ Sign user out and navigate to LoginFragment
                        auth.signOut()
                        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(
                    context,
                    "Signup Failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
