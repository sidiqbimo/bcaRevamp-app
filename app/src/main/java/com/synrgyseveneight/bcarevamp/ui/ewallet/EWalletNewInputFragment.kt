package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class EWalletNewInputFragment : Fragment() {

    private lateinit var header: ConstraintLayout
    private lateinit var backButton: ImageView
    private lateinit var titleTab: TextView
    private lateinit var containerAccountNumberInput: ConstraintLayout
    private lateinit var titleAccountNumberNewTarget: TextView
    private lateinit var accountNumberInput: EditText
    private lateinit var successSeekForAccountContainer: ConstraintLayout
    private lateinit var icSuccess: ImageView
    private lateinit var titleVerified: TextView
    private lateinit var titleSaveAsContact: CheckBox
    private lateinit var saveAsWhoContainer: ConstraintLayout
    private lateinit var saveAsEditText: EditText
    private lateinit var buttonStartTF: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_e_wallet_new_input, container, false)

        // Initialize views
        header = view.findViewById(R.id.header)
        backButton = view.findViewById(R.id.backButton)
        titleTab = view.findViewById(R.id.title_tab)
        containerAccountNumberInput = view.findViewById(R.id.container_accountnumberinput)
        titleAccountNumberNewTarget = view.findViewById(R.id.title_accountnumbernewtarget)
        accountNumberInput = view.findViewById(R.id.accountNumberInput)
        successSeekForAccountContainer = view.findViewById(R.id.successSeekForAccountContainer)
        icSuccess = view.findViewById(R.id.ic_success)
        titleVerified = view.findViewById(R.id.title_verified)
        titleSaveAsContact = view.findViewById(R.id.title_saveascontact)
        saveAsWhoContainer = view.findViewById(R.id.saveaswho_container)
        saveAsEditText = view.findViewById(R.id.saveas_edittext)
        buttonStartTF = view.findViewById(R.id.button_start_tf)

        // Set listeners
        backButton.setOnClickListener {
            // Handle back button click
            requireActivity().onBackPressed()
        }

        buttonStartTF.setOnClickListener {
            findNavController().navigate(R.id.action_eWalletNewInputFragment_to_eWalletInputFragment)
        }

        // Other logic and listeners can be added here

        return view
    }
}
