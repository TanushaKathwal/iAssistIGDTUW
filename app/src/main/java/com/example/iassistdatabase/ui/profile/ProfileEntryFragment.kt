package com.example.iassistdatabase.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentDummyBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileEntryFragment : Fragment() {

    private var _binding: FragmentDummyBinding? = null
    private val binding get() = _binding!!

    private var navigated = false  // Prevent multiple navigations
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDummyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        // Temporarily force logout for testing
       // auth.signOut()

        // Safe navigation after the view is attached
        binding.root.post {
            if (!navigated) {
                navigated = true
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    findNavController().navigate(
                        R.id.loginFragment,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.profileEntryFragment, true)
                            .build()
                    )

                } else {
                    findNavController().navigate(
                        R.id.profileFragment,
                        null,
                        androidx.navigation.NavOptions.Builder()
                            .setPopUpTo(R.id.profileEntryFragment, true)
                            .build()
                    )

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
