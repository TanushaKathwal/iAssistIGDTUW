package com.example.iassistdatabase.ui.roomfinder

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.iassistdatabase.R
import java.text.SimpleDateFormat
import java.util.*

class RoomFinderFragment : Fragment() {

    private lateinit var datePickerEditText: EditText
    private lateinit var spinnerTimeSlot: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_roomfinder, container, false)

        // Initialize views
        datePickerEditText = view.findViewById(R.id.datePickerEditText)
        spinnerTimeSlot = view.findViewById(R.id.spinnerTimeSlot)

        // Setup spinner
        val timeSlots = listOf(
            "Select Time", "9am-10am", "10am-11am", "11am-12pm",
            "12pm-1pm", "1pm-2pm", "2pm-3pm", "3pm-4pm", "4pm-5pm"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            timeSlots
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeSlot.adapter = adapter
        spinnerTimeSlot.setSelection(0)

        // Date picker click listener
        datePickerEditText.setOnClickListener {
            showDatePicker()
        }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val themedContext = ContextThemeWrapper(requireContext(), R.style.MyDatePickerDialogTheme)

        val datePickerDialog = DatePickerDialog(
            themedContext,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                datePickerEditText.setText(dateFormat.format(selectedDate.time))
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
