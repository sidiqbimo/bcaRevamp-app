package com.synrgyseveneight.bcarevamp.ui.qris

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


class QRISFailedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_i_s_failed, container, false)

        val exitButton = view.findViewById<ImageView>(R.id.btnBack)
        val repeatButton = view.findViewById<Button>(R.id.btnTopUp)

        exitButton.setOnClickListener {
            findNavController().apply {
                popBackStack(R.id.homeFragment, false)
                navigate(R.id.homeFragment)
            }
        }
        repeatButton.setOnClickListener {
            findNavController().apply {
                popBackStack(R.id.QRISCameraFragment, false)
                navigate(R.id.QRISCameraFragment)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().apply {
                popBackStack(R.id.homeFragment, false)
                navigate(R.id.homeFragment)
            }
        }
    }
}