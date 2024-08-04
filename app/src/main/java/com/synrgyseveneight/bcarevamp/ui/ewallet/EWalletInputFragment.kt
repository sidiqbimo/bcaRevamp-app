package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class EWalletInputFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_e_wallet_input, container, false)

        // Initialize UI elements
        val backButton: ImageView = view.findViewById(R.id.backButton)
        val titleTab: TextView = view.findViewById(R.id.title_tab)
        val tfInputTextNominal: EditText = view.findViewById(R.id.tf_inputtext_nominal)
        val errorMessageTf: TextView = view.findViewById(R.id.error_message_tf)
        val btnQuickOption1: Button = view.findViewById(R.id.btnQuickOption1)
        val btnQuickOption2: Button = view.findViewById(R.id.btnQuickOption2)
        val btnQuickOption3: Button = view.findViewById(R.id.btnQuickOption3)
        val btnQuickOption4: Button = view.findViewById(R.id.btnQuickOption4)
        val btnQuickOption5: Button = view.findViewById(R.id.btnQuickOption5)
        val btnQuickOption6: Button = view.findViewById(R.id.btnQuickOption6)
        val buttonStartTf: Button = view.findViewById(R.id.button_start_tf)

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        buttonStartTf.setOnClickListener {
            val inputNominal = tfInputTextNominal.text.toString()
            if (inputNominal.isEmpty()) {
                errorMessageTf.visibility = View.VISIBLE
                Toast.makeText(context, "Masukkan nominal", Toast.LENGTH_SHORT).show()
            } else {
                errorMessageTf.visibility = View.GONE
                findNavController().navigate(R.id.action_eWalletInputFragment_to_eWalletConfirmationFragment)
            }
        }

        val quickOptions = listOf(btnQuickOption1, btnQuickOption2, btnQuickOption3, btnQuickOption4, btnQuickOption5, btnQuickOption6)
        for (button in quickOptions) {
            button.setOnClickListener {
                tfInputTextNominal.setText(button.text.toString())
            }
        }

        return view
    }
}
