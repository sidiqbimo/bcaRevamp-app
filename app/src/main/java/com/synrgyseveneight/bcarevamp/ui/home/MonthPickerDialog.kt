package com.synrgyseveneight.bcarevamp.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.synrgyseveneight.bcarevamp.R
import java.util.Calendar

class MonthPickerDialog(
    private val context: Context,
    private val onDateSelected: (formattedDate: String, month: Int, year: Int) -> Unit
) {
    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.popup_number_picker, null)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)


        // Inisialisasi NumberPicker Bulan
        val months = arrayOfNulls<String>(12)
        for (i in 0 until 12) {
            months[i] = getMonthName(i + 1) // Fungsi untuk mendapatkan nama bulan
        }
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = months
        monthPicker.value = Calendar.getInstance().get(Calendar.MONTH)

        // Inisialisasi NumberPicker Tahun
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 3
        yearPicker.maxValue = currentYear + 0
        yearPicker.value = currentYear


        val materialAlertDialog = MaterialAlertDialogBuilder(context, R.style.dialog_theme_picker)
            .setTitle("Pilih Bulan :")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                selectedMonth = monthPicker.value + 1
                selectedYear = yearPicker.value

                val formattedDate = "${getMonthName(selectedMonth)} $selectedYear"
                onDateSelected(formattedDate, selectedMonth, selectedYear)
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .create()

        materialAlertDialog.show()
    }

    private fun getMonthName(month: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, context.resources.configuration.locale)
    }

}
