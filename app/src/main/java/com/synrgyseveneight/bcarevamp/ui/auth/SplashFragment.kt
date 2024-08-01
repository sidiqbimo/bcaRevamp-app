package com.synrgyseveneight.bcarevamp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gunakan coroutine untuk menunda navigasi
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000) // Tunda selama 2 detik (sesuaikan sesuai kebutuhan)
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }
}