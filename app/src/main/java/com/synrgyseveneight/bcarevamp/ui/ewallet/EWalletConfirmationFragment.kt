package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class EWalletConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_e_wallet_confirmation, container, false)

        // Find views by ID
        val backButton: ImageView = view.findViewById(R.id.backButton)
        val titleTab: TextView = view.findViewById(R.id.title_tab)
        val tvUserAccountDetails: TextView = view.findViewById(R.id.tvUserAccountDetails)
        val tvSourceAccountDetails: TextView = view.findViewById(R.id.tvSourceAccountDetails)
        val tvReceiverName: TextView = view.findViewById(R.id.tvReceiverName)
        val tvEWalletDestinationDetails: TextView = view.findViewById(R.id.tvEWalletDestinationDetails)
        val tvAmountDetails: TextView = view.findViewById(R.id.tvAmountDetails)
        val tvAdminFeeDetails: TextView = view.findViewById(R.id.tvAdminFeeDetails)
        val tvMessageDetails: TextView = view.findViewById(R.id.tvMessageDetails)
        val tvTotalDetails: TextView = view.findViewById(R.id.tvTotalDetails)
        val buttonStartTf: Button = view.findViewById(R.id.button_start_tf)

        // Set data to TextViews (just an example, you should set these with real data)
        tvUserAccountDetails.text = "Jhon Doe"
        tvSourceAccountDetails.text = "TAHAPAN BCA - 1234567890"
        tvReceiverName.text = "Anonim"
        tvEWalletDestinationDetails.text = "E-Wallet 1 - 0987654321"
        tvAmountDetails.text = "Rp 1,000,000"
        tvAdminFeeDetails.text = "Rp 5,000"
        tvMessageDetails.text = "Transfer for services"
        tvTotalDetails.text = "Rp 1,005,000"

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        buttonStartTf.setOnClickListener {
            findNavController().navigate(R.id.action_eWalletConfirmationFragment_to_pinFragment)
        }

        return view
    }
}
