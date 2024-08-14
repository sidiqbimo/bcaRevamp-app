package com.synrgyseveneight.bcarevamp.ui.mutation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.synrgyseveneight.bcarevamp.R
import java.util.Calendar

class MutationOptionFilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mutation_option_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonTransaction = view.findViewById<MaterialButton>(R.id.transaction_option_button)
        val optionTransaction = view.findViewById<ConstraintLayout>(R.id.list_trans)
        val transactionTypeRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_trans_option)
        val dateRangeRadioGroup = view.findViewById<RadioGroup>(R.id.radioGroup_date_option)
        val datePickerView = view.findViewById<LinearLayout>(R.id.date_picker_mutation)
        val buttonOptionDate1 = view.findViewById<Button>(R.id.date_option_button1)
        val buttonOptionDate2 = view.findViewById<Button>(R.id.date_option_button2)
        val btnTerapkan = view.findViewById<Button>(R.id.buttonTerapkan)


        optionTransaction.visibility = View.GONE
        datePickerView.visibility = View.GONE

        buttonTransaction.setOnClickListener {
            optionTransaction.visibility = View.VISIBLE
        }

        transactionTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<View>(checkedId) as? RadioButton
            val selectedText = selectedRadioButton?.text?.toString() ?: ""
            buttonTransaction.text = selectedText

            // Sembunyikan RadioGroup setelah pilihan dibuat
            optionTransaction.visibility = View.GONE
        }
        dateRangeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_date_picker) {
                // Tampilkan ImageView
                datePickerView.visibility = View.VISIBLE
            } else {
                // Sembunyikan ImageView jika RadioButton lain dipilih
                datePickerView.visibility = View.GONE
            }
        }
        buttonOptionDate1.setOnClickListener { showDatePicker(buttonOptionDate1) }
        buttonOptionDate2.setOnClickListener { showDatePicker(buttonOptionDate2) }
        btnTerapkan.setOnClickListener {activity?.onBackPressed()}

    }

    private fun showDatePicker(button: Button) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val
                datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSelected, monthOfYear,
              dayOfMonth ->
                val formattedDate =
                    String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, yearSelected)
                button.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

