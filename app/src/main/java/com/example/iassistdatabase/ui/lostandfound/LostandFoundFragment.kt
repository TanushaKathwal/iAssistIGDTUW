package com.example.iassistdatabase.ui.lostandfound

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.iassistdatabase.R
import android.widget.TableLayout
import com.example.iassistdatabase.databinding.FragmentLostandfoundBinding

class LostandFoundFragment : Fragment() {

    private var _binding: FragmentLostandfoundBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLostandfoundBinding.inflate(inflater, container, false)

        val notices = listOf(
            "Water Bottle - Red",
            "Earphones - Light Blue",
            "Wallet - Green",
            "ID Card - NA",
            "Lunch Box - Pink"
        )

        for ((index, notice) in notices.withIndex()) {
            val row = TableRow(requireContext())

            val numTextView = TextView(requireContext()).apply {
                text = (index + 1).toString()
                setPadding(10, 30, 10, 25)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.2f)
            }

            val noticeTextView = TextView(requireContext()).apply {
                text = notice
                setPadding(10, 30, 20, 25)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setTextColor(Color.BLACK)
                setSingleLine(false)
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            }

            row.addView(numTextView)
            row.addView(noticeTextView)
            binding.tableLayout.addView(row)
        }

        binding.reportButton.setOnClickListener {
            findNavController().navigate(R.id.action_lostandFoundFragment_to_reportLostFoundFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
