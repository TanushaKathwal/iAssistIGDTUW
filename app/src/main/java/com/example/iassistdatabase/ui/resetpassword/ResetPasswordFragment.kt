package com.example.iassistdatabase.ui.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ResetPasswordFragment : Fragment() {

    private lateinit var newPassword: EditText
    private lateinit var reenterPassword: EditText
    private lateinit var resetPasswordBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resetpassword, container, false)

        newPassword = view.findViewById(R.id.newPassword)
        reenterPassword = view.findViewById(R.id.reenterPassword)
        resetPasswordBtn = view.findViewById(R.id.resetPasswordBtn)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser ?: return view

        resetPasswordBtn.setOnClickListener {
            val pass1 = newPassword.text.toString()
            val pass2 = reenterPassword.text.toString()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (pass1 != pass2) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else if (pass1.length < 6) {
                Toast.makeText(context, "Password too short (min 6 chars)", Toast.LENGTH_SHORT).show()
            } else {
                updatePassword(pass1)
            }
        }

        return view
    }

    private fun updatePassword(newPass: String) {
        user.updatePassword(newPass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Password has been reset", Toast.LENGTH_SHORT).show()
                    // Navigate to Login Page
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
                } else {
                    Toast.makeText(context, "Password reset failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}