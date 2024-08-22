package com.synrgyseveneight.bcarevamp.ui.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class LoginSecondFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login_second, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        var signaturetext: String = ""
        val textName = view.findViewById<TextView>(R.id.textName)
        val textIdUser = view.findViewById<TextView>(R.id.textId)
        val buttonLogin = view.findViewById<TextView>(R.id.buttonLogin)
        val etPassword: EditText = view.findViewById(R.id.inputPasswordField)
        val tilPassword: TextInputLayout = view.findViewById(R.id.inputPassword)
        val txtErrorPw: TextView = view.findViewById(R.id.errorPwInput)
        val linePw: MaterialDivider = view.findViewById(R.id.garis)

        etPassword.transformationMethod = PasswordTransform()

        viewModelAuth.userName.observe(viewLifecycleOwner) { name ->
            textName.text = name
        }
        viewModelAuth.userSignature.observe(viewLifecycleOwner) { signature ->
            signaturetext = signature.toString()
            val maskedSignature = maskSignature(signature.toString())
            textIdUser.text = maskedSignature
        }


        buttonLogin.setOnClickListener {
            val password = etPassword.text.toString()
            val signature = signaturetext

            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showLoading(true) // Tampilkan loading
            viewModelAuth.signIn(signature, password, {
                showLoading(false) // Sembunyikan loading jika berhasil
                findNavController().navigate(R.id.action_loginSecondFragment_to_homeFragment)
            }, { error ->
                showLoading(false) // Sembunyikan loading jika gagal
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                tilPassword.setEndIconTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark))
                linePw.setDividerColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                txtErrorPw.visibility = TextView.VISIBLE
                etPassword.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            })

        }
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                txtErrorPw.visibility = TextView.GONE
                tilPassword.setEndIconTintList(ContextCompat.getColorStateList(requireContext(), R.color.Primary100))
                linePw.setDividerColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
                etPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.Primary100))
            }
            override fun afterTextChanged(s: Editable?) {}
        }
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
    private fun maskSignature(signature: String): String {
        if (signature.length < 5) {
            return signature
        }
        return signature[0] + "******" + signature.last()
    }
}