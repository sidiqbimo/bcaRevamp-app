package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R


class TransferFailedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_failed, container, false)

        val exitButton = view.findViewById<ImageView>(R.id.btnBack)
        val repeatButton = view.findViewById<Button>(R.id.btnTopUp)

        exitButton.setOnClickListener {
            findNavController().navigate(R.id.action_transferFailedFragment_to_transferListFragment)
        }
        repeatButton.setOnClickListener {
            findNavController().navigate(R.id.action_transferFailedFragment_to_transferListFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_transferFailedFragment_to_transferListFragment)
        }
    }

}