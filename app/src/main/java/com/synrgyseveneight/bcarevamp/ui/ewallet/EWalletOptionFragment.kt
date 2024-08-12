package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class EWalletOptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_e_wallet_option, container, false)

        // Initialize the buttons
        val btnOvo = view.findViewById<ImageButton>(R.id.btnOvo)
        val backButton = view.findViewById<ImageView>(R.id.backButton)

        // Set click listeners for each button
        btnOvo.setOnClickListener {
            // Handle OVO button click
            findNavController().navigate(R.id.action_eWalletOptionFragment_to_eWalletContactListFragment)
        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }
}
