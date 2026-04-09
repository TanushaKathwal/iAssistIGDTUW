package com.example.iassistdatabase.ui.reportlostfound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.iassistdatabase.R
import com.example.iassistdatabase.databinding.FragmentReportLostFoundBinding

class ReportLostFoundFragment : Fragment() {

    private var _binding: FragmentReportLostFoundBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportLostFoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dummy action for now
        binding.Savebutton.setOnClickListener {
            Toast.makeText(requireContext(), "Report Submitted (Dummy)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
