package com.mfarrelathaillahnugrohojsleepmn.imeonapps.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mfarrelathaillahnugrohojsleepmn.imeonapps.databinding.FragmentPopupAddTaskBinding
import java.text.SimpleDateFormat
import java.util.*


class PopupAddTaskFragment : DialogFragment() {

    private lateinit var binding : FragmentPopupAddTaskBinding
    private lateinit var listener : DialogNextBtnClickListener
    private val calendar = Calendar.getInstance()

    fun setListener(listener : DialogNextBtnClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPopupAddTaskBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }


    private fun registerEvents() {
        binding.inputButton.setOnClickListener {
            val taskname = binding.inputTaskname.text.toString()
            val taskdate = binding.inputTaskdeadline.text.toString()

            if (taskname.isNotEmpty()){
                listener.onSaveTask(taskname, binding.inputTaskname, taskdate, binding.inputTaskdeadline)
                println("Keklik bang")

            }else{
                Toast.makeText(context, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
                println("ke klik tetep bang")
            }
        }
        binding.inputTaskdeadline.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDateTimePicker()
            }
        }
    }

    private fun showDateTimePicker() {
        val currentDate = calendar.time
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                binding.inputTaskdeadline.setText(format.format(calendar.time)).toString()
            }

            TimePickerDialog(requireContext(), timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true).show()
        }

        DatePickerDialog(requireContext(), dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = currentDate.time
        }.show()
    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(nametask : String, taskinput : EditText, taskdate: String, taskdateinput: EditText)
    }

}