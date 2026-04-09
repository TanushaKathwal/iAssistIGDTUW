package com.example.iassistdatabase.ui.login

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var currentCaptcha: String = ""

    private fun generateCaptcha(): String {
        val chars = ('A'..'Z') + ('0'..'9') + ('a'..'z')
        return List(5) { chars.random() }.joinToString("")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Generate initial captcha
        currentCaptcha = generateCaptcha()
        binding.captchaText.text = currentCaptcha

        // Refresh captcha on text click
        binding.captchaText.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                currentCaptcha = generateCaptcha()
                binding.captchaText.text = currentCaptcha
            }
        }

        // Show/hide password
        binding.showPasswordCheck.setOnCheckedChangeListener { _, isChecked ->
            binding.passwordInput.inputType = if (isChecked)
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordInput.setSelection(binding.passwordInput.text.length)
        }

        // Login button logic
        binding.loginBtn.setOnClickListener {
            val enteredCaptcha = binding.captchaInput.text.toString()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (enteredCaptcha == currentCaptcha) {
                // Proceed with Firebase login
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
                        } else {
                            Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Captcha incorrect", Toast.LENGTH_SHORT).show()
                currentCaptcha = generateCaptcha()
                binding.captchaText.text = currentCaptcha
            }
        }


        // Navigate to SignupFragment
        binding.signupBold.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        // Navigate to ForgotPasswordFragment
        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
