package com.synrgyseveneight.bcarevamp.ui.error

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R

class Error404 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error404, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backToHomeButton: Button = view.findViewById(R.id.button_back_to_home)
        backToHomeButton.setOnClickListener{
            findNavController().navigate(R.id.action_error404Fragment_to_homeFragment)
        }
    }



}