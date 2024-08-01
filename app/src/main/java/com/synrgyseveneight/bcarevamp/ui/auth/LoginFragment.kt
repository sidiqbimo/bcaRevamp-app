package com.synrgyseveneight.bcarevamp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.synrgyseveneight.bcarevamp.R


class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogin: Button = view.findViewById(R.id.buttonLogin)
        btnLogin.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_homeFragment)
        )

    }


}