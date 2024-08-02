package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory

class TransferInputFragment : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_input, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
            }
        }

        viewModelAuth.userBalance.observe(viewLifecycleOwner) {
            Log.d("TransferInputFragment", "Balance updated: $it")
            val saldoAmount = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
            saldoAmount.text = "Rp $it"
        }

        val accountNumberText = view.findViewById<TextView>(R.id.accountnumbersubtitle)
        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            Log.d("TransferInputFragment", "Account updated: $it")
            accountNumberText.text = formattedAccountNumber(it ?: "Gagal memuat")
        }

        // Retrieve the data from the Bundle
        arguments?.let {
            val icon = it.getInt("icon")
            val name = it.getString("name")
            val accountType = it.getString("accountType")
            val accountNumber = it.getString("accountNumber")

            // Use these values to populate your UI elements
            val iconImageView = view.findViewById<ImageView>(R.id.account_photo)
            val nameTextView = view.findViewById<TextView>(R.id.accountholdername_title)
            val accountTypeTextView = view.findViewById<TextView>(R.id.accounttype)
            val accountNumberTextView = view.findViewById<TextView>(R.id.accountnumbersubtitle)

            iconImageView.setImageResource(icon)
            nameTextView.text = name
            accountTypeTextView.text = accountType
            accountNumberTextView.text = accountNumber
        }
    }

    private fun formattedAccountNumber(accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {
            formattedNoRek
        }
    }
}
