package com.synrgyseveneight.bcarevamp.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textfield.TextInputLayout
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory


class LoginFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private val viewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        val etSignature: EditText = view.findViewById(R.id.inputIdField)
        val etPassword: EditText = view.findViewById(R.id.inputPasswordField)
        val btnLogin: Button = view.findViewById(R.id.buttonLogin)
        val txtErrorId: TextView = view.findViewById(R.id.errorIdInput)
        val txtErrorPw: TextView = view.findViewById(R.id.errorPwInput)
        val lineId: MaterialDivider = view.findViewById(R.id.garis1)
        val linePw: MaterialDivider = view.findViewById(R.id.garis2)
        val tilPassword: TextInputLayout = view.findViewById(R.id.inputPassword)

        etPassword.transformationMethod = PasswordTransform()

        btnLogin.setOnClickListener {

            val signature = etSignature.text.toString()
            val password = etPassword.text.toString()

            // Validasi input sebelum melakukan sign-in
            if (signature.isEmpty()) {
                Toast.makeText(requireContext(), "User Id tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showLoading(true) // Tampilkan loading
            viewModel.signIn(signature, password, {
                showLoading(false) // Sembunyikan loading jika berhasil
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }, { error ->
                showLoading(false) // Sembunyikan loading jika gagal
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                progressBar = view.findViewById(R.id.progressBar)
                tilPassword.setEndIconTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark))
                lineId.setDividerColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                linePw.setDividerColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                txtErrorId.visibility = TextView.VISIBLE
                etSignature.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                txtErrorPw.visibility = TextView.VISIBLE
                etPassword.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            })
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtErrorId.visibility = TextView.GONE
                txtErrorPw.visibility = TextView.GONE
                tilPassword.setEndIconTintList(ContextCompat.getColorStateList(requireContext(), R.color.Primary100))
                lineId.setDividerColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
                linePw.setDividerColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
                etSignature.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
                etPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        etSignature.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)


        tilPassword.setEndIconOnClickListener {
            val isPasswordVisible = etPassword.transformationMethod == null
            if (isPasswordVisible) {
                etPassword.transformationMethod = PasswordTransform()
                etPassword.setSelection(etPassword.text.length)
            } else {
                etPassword.transformationMethod = null
                etPassword.setSelection(etPassword.text.length)
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}