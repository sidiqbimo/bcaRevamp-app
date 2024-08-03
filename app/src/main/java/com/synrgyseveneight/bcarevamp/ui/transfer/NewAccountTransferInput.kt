package com.synrgyseveneight.bcarevamp.ui.transfer

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import java.text.NumberFormat
import java.util.Locale

class NewAccountTransferInput : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private var balance: Double = 0.0
    private var balanceWithRupiah : String = "Sedang mengambil data saldo..."
    private var retryCountForSaldo = 0

    private var accountNumber: String = ""
    private var bankNameType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_account_transfer_input, container, false)

        // Find the EditText and the containers
        val accountNumberInput = view.findViewById<EditText>(R.id.accountNumberInput)
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

        val accountNumberMock = "3344556677"

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
        accountNumberInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // No action needed here
            }

            override fun afterTextChanged(s: Editable) {
                // If the input matches 2233445566, show the containers
                if (s.toString() == accountNumberMock) {
                    successSeekForAccountContainer.visibility = View.VISIBLE
                    containerSumberRekening.visibility = View.VISIBLE
                    separatorTf.visibility = View.VISIBLE
                    separatorTftonominal.visibility = View.VISIBLE
                    transferNominalContainer.visibility = View.VISIBLE
                    buttonStartTransfer.visibility = View.VISIBLE

                    // Enable Button when verified
                    buttonStartTransfer.isEnabled = true
                    buttonStartTransfer.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_roundedrectangle)
                } else {
                    // If the input does not match 2233445566, hide the containers
                    successSeekForAccountContainer.visibility = View.GONE
                    containerSumberRekening.visibility = View.GONE
                    separatorTf.visibility = View.GONE
                    separatorTftonominal.visibility = View.GONE
                    transferNominalContainer.visibility = View.GONE
                    buttonStartTransfer.visibility = View.GONE

                }
            }
        })

        // Set an OnEditorActionListener on the EditText
        accountNumberInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // If the enter key is pressed, perform the validation
                if (accountNumberInput.text.toString() == accountNumberMock) {
                    successSeekForAccountContainer.visibility = View.VISIBLE
                    containerSumberRekening.visibility = View.VISIBLE
                    separatorTf.visibility = View.VISIBLE
                    separatorTftonominal.visibility = View.VISIBLE
                    transferNominalContainer.visibility = View.VISIBLE
                } else {
                    successSeekForAccountContainer.visibility = View.GONE
                    containerSumberRekening.visibility = View.GONE
                    separatorTf.visibility = View.GONE
                    separatorTftonominal.visibility = View.GONE
                    transferNominalContainer.visibility = View.GONE
                }

                // Hide the keyboard
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                true
            } else {
                false
            }
        }

        // Find the checkbox and the container
        val checkBox = view.findViewById<CheckBox>(R.id.title_saveascontact)
        val saveAsWhoContainer = view.findViewById<View>(R.id.saveaswho_container)

        // Initially hide the container
        saveAsWhoContainer.visibility = View.GONE

        // Set a listener on the checkbox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // If the checkbox is checked, show the container
                saveAsWhoContainer.visibility = View.VISIBLE
            } else {
                // If the checkbox is not checked, hide the container
                saveAsWhoContainer.visibility = View.GONE
            }
        }

//        Titik setiap tiga digit di edittext nominal
        transferNominalInput.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                val inputtedValue = s.toString().replace(".", "").toDoubleOrNull() ?: 0.0

                if (s.toString() != current) {
                    transferNominalInput.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(".", "")

                    // Check if cleanString is not empty
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(parsed)

                        current = formatted
                        transferNominalInput.setText(formatted)
                        transferNominalInput.setSelection(formatted.length)
                    }

                    transferNominalInput.addTextChangedListener(this)

                    // Compare the inputted value with the balance
                    if (inputtedValue != null && inputtedValue > balance) {
                        // If the inputted value is greater, change the background and show the error message
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_redstroke)
                        errortext.text = "Saldo Anda tidak mencukupi, mohon isikan kembali"
                        errortext.visibility = View.VISIBLE
                    } else if (inputtedValue != null && inputtedValue > 0 && inputtedValue <= balance){
                        // If the inputted value is not greater, revert the background and hide the error message
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_greenstroke)
                        errortext.visibility = View.GONE
                    } else if (inputtedValue != null && inputtedValue == 0.0){
                        // If the inputted value is empty, revert the background and hide the error message
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround)
                        errortext.text = "Minimum transfer adalah Rp 1, mohon isikan kembali"
                        errortext.visibility = View.GONE
                    } else if (inputtedValue == null) {
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround)
                        errortext.visibility = View.GONE
                    }
                }
            }
        })

        // Hide keyboard kalau udah selesai masukkin nominal TF
        transferNominalInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // TODO: Kalau saldo kurang, nanti teks peringatan muncul dan background jadi merah
                if (transferNominalInput.text.toString() == accountNumberMock) {
                    errortext.visibility = View.VISIBLE
                } else {
                    errortext.visibility = View.GONE
                }

                // Hide the keyboard
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                true
            } else {
                false
            }
        }

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saldoTFNew = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
        val myaccountNumberText = view.findViewById<TextView>(R.id.accountnumbersubtitle)

        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
            }
        }

        viewModelAuth.userBalance.observe(viewLifecycleOwner) {
            Log.d("NewTransferNominalInput", "Balance updated: $it")
            val saldoAmount = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)

            if (it==null){
                retryCountForSaldo++
                saldoTFNew.contentDescription = "Saldo sedang dimuat, mohon tunggu"
                if (retryCountForSaldo <= 3) {
                    viewModelAuth.fetchBalance(viewModelAuth.userToken.value?:"")
                } else {
                    // Kalau udah tiga kali masih null, balik ke Beranda
                    Toast.makeText(context, "Gagal memuat saldo. Silakan coba lagi", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_newAccountTransferInput_to_homeFragment)


                }
            } else {
                retryCountForSaldo = 0
                saldoAmount.text = "Rp ${it}"
                val cleanstring = it.toString().replace(".", "")
                balance = cleanstring.toDouble()
                saldoTFNew.contentDescription = "Saldo saat ini adalah ${balanceWithRupiah}"
            }
        }

        val accountNumberText = view.findViewById<TextView>(R.id.accountnumbersubtitle)
        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            Log.d("NewTransferNominalInput", "Account updated: $it")

            val accountNumberWithDash = formattedAccountNumber(it?: "Gagal memuat")
            accountNumberText.text = accountNumberWithDash

            accountNumber = it.toString()

            if (accountNumber == null){
                myaccountNumberText.contentDescription = "Nomor rekening sumber dana transfer gagal ditampilkan, sedang mencoba kembali"
            } else {
                myaccountNumberText.contentDescription = "Nomor rekening sumber dana transfer adalah ${accountNumberWithDash}"
            }
        }

        viewModelAuth.userBankName.observe(viewLifecycleOwner) {
            val bankName = view.findViewById<TextView>(R.id.accounttype)
            bankName.text = it

            bankNameType = it.toString()
        }
    }

    private fun formattedAccountNumber (accountNumber: String): String {
        val cleanStringNomorRekening = accountNumber.replace("-", "")
        val parsedNoRek = cleanStringNomorRekening.toString()
        val formattedNoRek = parsedNoRek.replace(Regex("(\\d{4})"), "$1-")
        return if (formattedNoRek.endsWith("-")) {
            formattedNoRek.dropLast(1)
        } else {formattedNoRek}
    }

}