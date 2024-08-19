package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import com.synrgyseveneight.bcarevamp.viewmodel.EWalletViewModel
import java.text.NumberFormat
import java.util.Locale

class EWalletNewInputFragment : Fragment() {

    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private lateinit var viewModelEWallet : EWalletViewModel

    private var balance: Double = 0.0
    private var retryCountForSaldo = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_e_wallet_new_input, container, false)

        // Define UI elements
        val backButton: ImageView = view.findViewById(R.id.backButton)
        val ewalletNumberInput = view.findViewById<EditText>(R.id.accountNumberInput)
        val successSeekForAccountContainer = view.findViewById<View>(R.id.successSeekForAccountContainer)
        val containerSumberRekening = view.findViewById<View>(R.id.container_sumberrekening)
        val separatorTf = view.findViewById<View>(R.id.separator_tf)
        val separatorTftonominal = view.findViewById<View>(R.id.separator_tftonominal)
        val transferNominalContainer = view.findViewById<View>(R.id.transfernominal_container)
        val errortext = view.findViewById<TextView>(R.id.error_message_tf)

        val transferNominalInput = view.findViewById<EditText>(R.id.tf_inputtext_nominal)

        val edittextContainerNominalInput = view.findViewById<View>(R.id.tf_inputnominal_container)
        val buttonStartTransfer = view.findViewById<Button>(R.id.button_start_tf)

        val eyeToggleTFNew = view.findViewById<ImageView>(R.id.toggleView)
        val censoredSaldoTFNew = view.findViewById<TextView>(R.id.censoredSaldoCheck)
        val saldoTFNew = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
        val myaccountNumberText = view.findViewById<TextView>(R.id.accountnumbersubtitle)

        val ewalletNumberMock = "081234567890" // E-wallet number mock for validation

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        buttonStartTransfer.setOnClickListener {
            if (validateInputs()) {
                // Navigate to confirmation page or handle transfer logic here
                findNavController().navigate(R.id.action_eWalletNewInputFragment_to_eWalletConfirmationFragment)
            } else {
                Toast.makeText(requireContext(), "Please correct the input errors", Toast.LENGTH_SHORT).show()
            }
        }

        // Show the balance first
        censoredSaldoTFNew.visibility = View.GONE
        saldoTFNew.visibility = View.VISIBLE

        // Eye Toggle Hide View
        eyeToggleTFNew.setOnClickListener {
            if (censoredSaldoTFNew.visibility == View.VISIBLE) {
                censoredSaldoTFNew.visibility = View.GONE
                saldoTFNew.visibility = View.VISIBLE
                eyeToggleTFNew.setImageResource(R.drawable.icon_blueeye_open)
                eyeToggleTFNew.contentDescription = "Saldo saat ini adalah ${saldoTFNew.text}. Klik dua kali untuk menyembunyikan saldo"
                it.announceForAccessibility("Saldo ditampilkan. Saldo saat ini adalah ${saldoTFNew.text}. Klik dua kali untuk menyembunyikan saldo")
            } else {
                censoredSaldoTFNew.visibility = View.VISIBLE
                saldoTFNew.visibility = View.GONE
                eyeToggleTFNew.setImageResource(R.drawable.icon_blueeyeclose)
                eyeToggleTFNew.contentDescription = "Saldo disembunyikan. Klik dua kali untuk menampilkan saldo"
                it.announceForAccessibility("Saldo disembunyikan. Klik dua kali untuk menampilkan saldo")
            }
        }

        // Initially hide the containers
        successSeekForAccountContainer.visibility = View.GONE
        containerSumberRekening.visibility = View.GONE
        separatorTf.visibility = View.GONE
        separatorTftonominal.visibility = View.GONE
        transferNominalContainer.visibility = View.GONE

        errortext.visibility = View.GONE
        buttonStartTransfer.visibility = View.GONE

        // Add a TextWatcher to the EditText
        ewalletNumberInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString() == ewalletNumberMock) {
                    visibleWhenVerified()
                } else {
                    goneWhenNotVerified()
                }
            }
        })

        // Set an OnEditorActionListener on the EditText
        ewalletNumberInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Hide the keyboard
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Checkbox logic
        val checkBox = view.findViewById<CheckBox>(R.id.title_saveascontact)
        val saveAsWhoContainer = view.findViewById<View>(R.id.saveaswho_container)

        saveAsWhoContainer.visibility = View.GONE

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            saveAsWhoContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Nominal input logic
        transferNominalInput.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                val inputtedValue = s.toString().replace(".", "").toDoubleOrNull() ?: 0.0

                if (s.toString() != current) {
                    transferNominalInput.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(".", "")

                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(parsed)

                        current = formatted
                        transferNominalInput.setText(formatted)
                        transferNominalInput.setSelection(formatted.length)
                    }

                    transferNominalInput.addTextChangedListener(this)

                    if (s.isNullOrEmpty()) {
                        buttonStartTransfer.visibility = View.GONE
                        buttonStartTransfer.isEnabled = false
                    }

                    // Validate balance and input
                    when {
                        inputtedValue > balance -> {
                            edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_redstroke)
                            errortext.text = "Saldo Anda tidak mencukupi, mohon isikan kembali"
                            transferNominalInput.announceForAccessibility("Saldo Anda tidak mencukupi, mohon isikan kembali")
                            errortext.visibility = View.VISIBLE
                            buttonStartTransfer.isEnabled = false
                            buttonStartTransfer.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_greyrectangle)
                        }
                        inputtedValue > 0 && inputtedValue <= balance -> {
                            edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_greenstroke)
                            errortext.visibility = View.GONE
                            buttonStartTransfer.isEnabled = true
                            buttonStartTransfer.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_roundedrectangle)
                            buttonStartTransfer.visibility = View.VISIBLE
                        }
                        inputtedValue == 0.0 -> {
                            edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_redstroke)
                            errortext.text = "Minimum transfer adalah Rp 1, mohon isikan kembali"
                            errortext.visibility = View.VISIBLE
                            buttonStartTransfer.isEnabled = false
                            buttonStartTransfer.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_greyrectangle)
                            transferNominalInput.announceForAccessibility("Minimum transfer adalah Rp 1, mohon isikan kembali")
                        }
                        else -> {
                            edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround)
                            errortext.visibility = View.GONE
                        }
                    }
                }
            }
        })

        // Hide keyboard after inputting nominal
        transferNominalInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                true
            } else {
                false
            }
        }

        // Logic for when the "Start Transfer" button is clicked
        buttonStartTransfer.setOnClickListener {
            if (validateInputs()) {
                // Navigate to confirmation page or handle transfer logic here
                findNavController().navigate(R.id.action_eWalletNewInputFragment_to_eWalletConfirmationFragment)
            } else {
                Toast.makeText(requireContext(), "Please correct the input errors", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    // Function to validate inputs before proceeding
    private fun validateInputs(): Boolean {
        val ewalletNumber = view?.findViewById<EditText>(R.id.accountNumberInput)?.text.toString()
        val transferAmount = view?.findViewById<EditText>(R.id.tf_inputtext_nominal)?.text.toString().replace(".", "").toDoubleOrNull() ?: 0.0

        if (ewalletNumber.isEmpty()) {
            Toast.makeText(requireContext(), "E-wallet number is required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (transferAmount <= 0) {
            Toast.makeText(requireContext(), "Transfer amount must be greater than zero", Toast.LENGTH_SHORT).show()
            return false
        }

        if (transferAmount > balance) {
            Toast.makeText(requireContext(), "Insufficient balance", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Show necessary elements when e-wallet number is verified
    private fun visibleWhenVerified() {
        val successSeekForAccountContainer = view?.findViewById<View>(R.id.successSeekForAccountContainer)
        val containerSumberRekening = view?.findViewById<View>(R.id.container_sumberrekening)
        val separatorTf = view?.findViewById<View>(R.id.separator_tf)
        val separatorTftonominal = view?.findViewById<View>(R.id.separator_tftonominal)
        val transferNominalContainer = view?.findViewById<View>(R.id.transfernominal_container)
        val ewalletNumberInput = view?.findViewById<EditText>(R.id.accountNumberInput)
        val buttonStartTransfer = view?.findViewById<View>(R.id.button_start_tf)

        successSeekForAccountContainer?.visibility = View.VISIBLE
        containerSumberRekening?.visibility = View.VISIBLE
        separatorTf?.visibility = View.VISIBLE
        separatorTftonominal?.visibility = View.VISIBLE
        transferNominalContainer?.visibility = View.VISIBLE
        buttonStartTransfer?.visibility = View.VISIBLE

        ewalletNumberInput?.isEnabled = false
    }

    // Hide elements when e-wallet number is not verified
    private fun goneWhenNotVerified() {
        val successSeekForAccountContainer = view?.findViewById<View>(R.id.successSeekForAccountContainer)
        val containerSumberRekening = view?.findViewById<View>(R.id.container_sumberrekening)
        val separatorTf = view?.findViewById<View>(R.id.separator_tf)
        val separatorTftonominal = view?.findViewById<View>(R.id.separator_tftonominal)
        val transferNominalContainer = view?.findViewById<View>(R.id.transfernominal_container)
        val ewalletNumberInput = view?.findViewById<EditText>(R.id.accountNumberInput)
        val buttonStartTransfer = view?.findViewById<View>(R.id.button_start_tf)

        successSeekForAccountContainer?.visibility = View.GONE
        containerSumberRekening?.visibility = View.GONE
        separatorTf?.visibility = View.GONE
        separatorTftonominal?.visibility = View.GONE
        transferNominalContainer?.visibility = View.GONE
        buttonStartTransfer?.visibility = View.GONE


        ewalletNumberInput?.isEnabled = true
    }
}

