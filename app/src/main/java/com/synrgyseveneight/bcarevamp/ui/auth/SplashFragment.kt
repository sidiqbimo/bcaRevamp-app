package com.synrgyseveneight.bcarevamp.ui.auth

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.userToken.observe(viewLifecycleOwner) { token ->
                if (token != null) {
                    // Jika token ada, navigasi ke HomeFragment
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    viewModel.userSignature.observe(viewLifecycleOwner) { signature ->
                        if (signature != null) {
                            // Jika signature ada, navigasi ke LoginSecondedFragment
                            findNavController().navigate(R.id.action_splashFragment_to_loginSecondFragment)
                        }else{
                            // Jika token tidak ada, navigasi ke LoginFragment
                            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                        }
                    }

                }
            }
        }, 3000) // Menunggu 3 detik
    }
}