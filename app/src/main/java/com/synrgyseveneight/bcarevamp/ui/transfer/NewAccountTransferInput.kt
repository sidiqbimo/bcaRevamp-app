package com.synrgyseveneight.bcarevamp.ui.transfer

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
import com.synrgyseveneight.bcarevamp.viewmodel.TransferViewModel
import java.text.NumberFormat
import java.util.Locale

class NewAccountTransferInput : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    //for validation
    private lateinit var viewModelTransfer : TransferViewModel

    private var balance: Double = 0.0
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
                    visibleWhenVerfiied()
                } else {
                    goneWhenNotVerified()
                }
            }
        })

        // Set an OnEditorActionListener on the EditText
        accountNumberInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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

        /*
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

         */

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


                    if (s.isNullOrEmpty()==true) {
                        buttonStartTransfer.visibility = View.GONE
                        buttonStartTransfer.isEnabled = false
                    }

                    // Compare the inputted value with the balance
                    if (inputtedValue != null && inputtedValue > balance) {
                        // If the inputted value is greater, change the background and show the error message
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_redstroke)
                        Log.d("NewAccountTransferInput", "uang ga cukup${inputtedValue}")
                        errortext.text = "Saldo Anda tidak mencukupi, mohon isikan kembali"
                        transferNominalInput.announceForAccessibility("Saldo Anda tidak mencukupi, mohon isikan kembali")
                        errortext.visibility = View.VISIBLE
                        buttonStartTransfer?.isEnabled = false
                        buttonStartTransfer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_greyrectangle)
                    } else if (inputtedValue != null && inputtedValue > 0 && inputtedValue <= balance){
                        // If the inputted value is not greater, revert the background and hide the error message
                        Log.d("NewAccountTransferInput", "uang cukup ${inputtedValue}")
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_greenstroke)
                        errortext.visibility = View.GONE
                        buttonStartTransfer?.isEnabled = true
                        buttonStartTransfer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_roundedrectangle)
                        buttonStartTransfer?.visibility = View.VISIBLE
                    } else if (inputtedValue != null && inputtedValue == 0.0){
                        // If the inputted value is empty, revert the background and hide the error message
                        Log.d("NewAccountTransferInput", "input nol, ${inputtedValue}")
                        edittextContainerNominalInput.setBackgroundResource(R.drawable.whitebackground_bottomnoround_redstroke)
                        errortext.text = "Minimum transfer adalah Rp 1, mohon isikan kembali"
                        errortext.visibility = View.VISIBLE
                        buttonStartTransfer?.isEnabled = false
                        buttonStartTransfer?.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_greyrectangle)
                        transferNominalInput.announceForAccessibility("Minimum transfer adalah Rp 1, mohon isikan kembali")
                    }  else if (inputtedValue == null) {
                        Log.d("NewAccountTransferInput", "gajadi masuk ${inputtedValue}")
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

        // Validation check
        viewModelTransfer = ViewModelProvider(this).get(TransferViewModel::class.java)
        val accountNumberInput = view.findViewById<EditText>(R.id.accountNumberInput)
        val successSeekForAccountContainer = view.findViewById<View>(R.id.successSeekForAccountContainer)
        val saveAsEditText = view.findViewById<EditText>(R.id.saveas_edittext)

        val saldoTFNew = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
        val myaccountNumberText = view.findViewById<TextView>(R.id.accountnumbersubtitle)

        var mytoken = ""

        // Check validate account target
        accountNumberInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Call API to verify account number
                if (s != null && s.length == 10) { // Assuming account number is 10 digits
                    viewModelTransfer.searchAccount(viewModelAuth.userToken.value?:"",s.toString())

                    // TOKEN TRANSFER IS HERE
                    mytoken = viewModelAuth.userToken.value?:""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Tampilkan inputtan tf kalau account number sudah verified
        viewModelTransfer.accountData.observe(viewLifecycleOwner, Observer { accountData ->
            if (accountData != null) {
                visibleWhenVerfiied()
                saveAsEditText.setText(accountData.name)
                accountNumberInput.announceForAccessibility("Nomor rekening terverifikasi atas nama ${accountData.name}")
            }
        })

        viewModelTransfer.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                goneWhenNotVerified()
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        // Start Transfer to be confirmed
        val nextButton = view.findViewById<Button>(R.id.button_start_tf)

        nextButton.setOnClickListener {
            val accountNumberTargetTransfer = view.findViewById<EditText>(R.id.accountNumberInput)
            val accountNumberSenderTransfer = view.findViewById<TextView>(R.id.accountnumbersubtitle)
            val amountTransfer = view.findViewById<EditText>(R.id.tf_inputtext_nominal).text.toString()
            val noteTransfer = view.findViewById<EditText>(R.id.tf_inputtext_notes).text.toString()
            val mytokenTransfer = mytoken

            // TODO : Save to daftar tersimpan

            // Navigate to next screen tp bawa value
            val action = NewAccountTransferInputDirections.actionNewAccountTransferInputToTransferConfirmationFragment(
                accountNumberTargetTransfer.text.toString(),
                amountTransfer,
                noteTransfer,
                accountNumberSenderTransfer.text.toString(),
                mytokenTransfer,
                avatarSenderPath = "",
                avatarTargetPath = "",
                bankReceiver = "",
                bankSender = "",
            )
            findNavController().navigate(action)
        }

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

                val balanceWithRupiah : String = it.toString().let {
                    val localID = Locale("id", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localID)
                    numberFormat.format(balance.toDouble())
                }
                saldoTFNew.contentDescription = "Saldo saat ini adalah ${balance} rupiah"
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

    private fun visibleWhenVerfiied (){
        val successSeekForAccountContainer = view?.findViewById<View>(R.id.successSeekForAccountContainer)
        val containerSumberRekening = view?.findViewById<View>(R.id.container_sumberrekening)
        val separatorTf = view?.findViewById<View>(R.id.separator_tf)
        val separatorTftonominal = view?.findViewById<View>(R.id.separator_tftonominal)
        val transferNominalContainer = view?.findViewById<View>(R.id.transfernominal_container)
        val buttonStartTransfer = view?.findViewById<Button>(R.id.button_start_tf)

        successSeekForAccountContainer?.visibility = View.VISIBLE
        containerSumberRekening?.visibility = View.VISIBLE
        separatorTf?.visibility = View.VISIBLE
        separatorTftonominal?.visibility = View.VISIBLE
        transferNominalContainer?.visibility = View.VISIBLE

//        if (buttonStartTransfer != null) {
//            buttonStartTransfer.visibility = View.VISIBLE
//        }


    }

    private fun goneWhenNotVerified (){
        val successSeekForAccountContainer = view?.findViewById<View>(R.id.successSeekForAccountContainer)
        val containerSumberRekening = view?.findViewById<View>(R.id.container_sumberrekening)
        val separatorTf = view?.findViewById<View>(R.id.separator_tf)
        val separatorTftonominal = view?.findViewById<View>(R.id.separator_tftonominal)
        val transferNominalContainer = view?.findViewById<View>(R.id.transfernominal_container)
        val buttonStartTransfer = view?.findViewById<Button>(R.id.button_start_tf)

        successSeekForAccountContainer?.visibility = View.GONE
        containerSumberRekening?.visibility = View.GONE
        separatorTf?.visibility = View.GONE
        separatorTftonominal?.visibility = View.GONE
        transferNominalContainer?.visibility = View.GONE

        // Disable Button when verified
        buttonStartTransfer?.visibility = View.GONE
    }

}