package com.synrgyseveneight.bcarevamp.ui.qris

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import com.synrgyseveneight.bcarevamp.viewmodel.QRISViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.TransferViewModel
import java.text.NumberFormat
import java.util.Locale

class QRISNominalInputFragment : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private lateinit var viewModelQris: QRISViewModel
    private lateinit var viewModelTransfer : TransferViewModel
    private lateinit var viewModelTransferCheckSender : TransferViewModel
    private val args: QRISCameraFragmentArgs by navArgs()

    private var balance: Double = 0.0
    private var retryCountForSaldo = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_i_s_nominal_input, container, false)

        // Find the EditText and the containers
        val containerSumberRekening = view.findViewById<View>(R.id.container_sumberrekening)
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
            findNavController().navigate(R.id.action_QRISNominalInputFragment_to_homeFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrisId = args.idQris
        var mytoken = args.token
        Log.d("QRISNominalInputFragment", "QRIS ID: $qrisId and get token $mytoken")

        // components to be changed
        val merchantName = view.findViewById<TextView>(R.id.merchant_name)
        val merchantLocation = view.findViewById<TextView>(R.id.merchant_location)
        val accountSenderType = view.findViewById<TextView>(R.id.accounttype)
        val accountNumber = view.findViewById<TextView>(R.id.accountnumbersubtitle)
        val saldoSender = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
        var nominalInput = view.findViewById<TextView>(R.id.tf_inputtext_nominal)
        val nextButton = view.findViewById<TextView>(R.id.button_start_tf)
        val merchantImage = view.findViewById<ImageView>(R.id.circularImageViewMerchant)

        var imagePathMerchant : String = ""
        var accountSenderName : String = ""
        var imagePathSender : String = ""
        var nmid : String = ""
        var terminalId : String = ""

        viewModelQris = ViewModelProvider(this).get(QRISViewModel::class.java)
        viewModelTransferCheckSender = ViewModelProvider(this).get(TransferViewModel::class.java)
        viewModelTransfer = ViewModelProvider(this).get(TransferViewModel::class.java)


        // GET Account Number
        viewModelAuth.userAccountNumber.observe(viewLifecycleOwner) {
            Log.d("QRISNominalInputFragment", "Account updated: $it")
            accountNumber.text = formattedAccountNumber(it?: "Gagal memuat")
        }

        // GET Balance
        viewModelAuth.userBalance.observe(viewLifecycleOwner) {
            Log.d("QRISNominalInputFragment", "Balance updated: $it")
            val saldoAmount = view.findViewById<TextView>(R.id.saldotf_BCA_tersisa)
            saldoAmount.text = "Rp ${it}"
            if (it==null){
                retryCountForSaldo++
                saldoSender.contentDescription = "Saldo sedang dimuat, mohon tunggu"
                if (retryCountForSaldo <= 3) {
                    viewModelAuth.fetchBalance(viewModelAuth.userToken.value?:"")
                } else {
                    // Kalau udah tiga kali masih null, balik ke Beranda
                    Toast.makeText(context, "Gagal memuat saldo. Silakan coba lagi", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_QRISNominalInputFragment_to_homeFragment)
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
                saldoSender.contentDescription = "Saldo saat ini adalah ${balance} rupiah"
            }
        }

        // GET Qris Data
        viewModelQris.qrisData.observe(viewLifecycleOwner) { qrisData ->
            if (qrisData != null) {
                merchantName.text = qrisData.name
                merchantLocation.text = qrisData.address
                nominalInput.text = qrisData.amount?.toString() ?: ""
                imagePathMerchant = qrisData.image_path ?: ""
                nmid = qrisData.nmid ?: ""
                terminalId = qrisData.terminal_id ?: ""

                Glide.with(this)
                    .load(imagePathMerchant)
                    .circleCrop()
                    .error(R.drawable.icon_person)
                    .into(merchantImage)
            }
        }

        // Get sender info
        viewModelTransferCheckSender.senderAccountData.observe(viewLifecycleOwner) { senderAccountData ->
            if (senderAccountData != null) {
                Log.d("QRISNominalInputFragment", "REACHED 326 Sender Account Data: $senderAccountData")
                accountSenderName = senderAccountData.name
                accountSenderType.text = senderAccountData.bank

                Log.d("QRISNominalInputFragment", "All data get to the LIINE 121 or SENDER : $senderAccountData")
                Log.d("QRISNominalInputFragment", "Image  path : $imagePathSender")

            } else Log.d("QRISNominalInputFragment", "Sender Account Data is null but went to senderAccountdata observer")
        }



        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
                Log.d("QRISNominalInputFragment", "GETTING TOKEN Token: $token")
                mytoken = token

                // Call viewmodel
                if (mytoken != null) {
                    //SearchQRIS
                    viewModelQris.searchQris(mytoken, qrisId)
                    Log.d("QRISNominalInputFragment", "Token is $mytoken")

                    //Balance
                    viewModelAuth.fetchBalance(token)
                    Log.d("QRISNominalInputFragment", "Token is $mytoken to check")

                    // Get sender profile
                    viewModelAuth.userImagePath.observe(viewLifecycleOwner) {
                        Log.d("QRISNominalInputFragment", "Image path updated: $it")
                        imagePathSender = it ?: ""
                    }

                    // Get sender name
                    viewModelAuth.userName.observe(viewLifecycleOwner) {
                        Log.d("QRISNominalInputFragment", "Name updated: $it")
                        accountSenderName = it ?: ""
                    }

                    //SearchSenderAccount
                    Log.d("QRISNominalInputFragment", "Account number used for search: ${accountNumber.text.toString().replace("-", "")}")
                    viewModelTransferCheckSender.searchSenderAccount(token,accountNumber.toString().replace("-", ""))
                    Log.d("QRISNominalInputFragment", "Sender name is $accountSenderName and image path is $imagePathSender with ")
                } else {
                    Toast.makeText(context, "Gagal memuat. Silakan coba lagi", Toast.LENGTH_SHORT).show()
                    Log.d("QRISNominalInputFragment", "Token is $mytoken")
                    findNavController().navigate(R.id.action_QRISNominalInputFragment_to_homeFragment)
                }



            }
        }


        // Error handling
        viewModelQris.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                Log.d("QRISNominalInputFragment", "Error: $error")
            }
        }



        // Error Handling Check Sender Account
        viewModelTransfer.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
//                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                Log.d("QRISNominalInputFragment", "Error: $error")
            }
        })
        viewModelTransferCheckSender.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
//                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                Log.d("QRISNominalInputFragment", "Error: $error")
            }
        })

        // Confirm QRIS Transaction
        val nextButtonGo = view.findViewById<Button>(R.id.button_start_tf)

        nextButtonGo.setOnClickListener{
            val noteTransfer = view.findViewById<EditText>(R.id.tf_inputtext_notes).text.toString()
            val accountNumberTargetTransfer = view.findViewById<EditText>(R.id.accountNumberInput)
            val accountNumberSenderTransfer = view.findViewById<TextView>(R.id.accountnumbersubtitle)
            val amountTransfer = view.findViewById<EditText>(R.id.tf_inputtext_nominal).text.toString()
            val merhchantNameText = view.findViewById<TextView>(R.id.merchant_name)

            val action = QRISNominalInputFragmentDirections.actionQRISNominalInputFragmentToQrisPinFragment(
                qrisId,
                mytoken,
                nominalInput.text.toString().replace(".",""),
                noteTransfer,
                accountNumberSenderTransfer.text.toString().replace("-", ""),
                imagePathSender,
                imagePathMerchant,
                accountSenderName.toString(),
                merhchantNameText.text.toString(),
                accountSenderType.text.toString(),
                nmid,
                terminalId
            )
            Log.d("QRISNominalInputFragment", "All data taken : qris ID  $accountNumberSenderTransfer \n $imagePathSender \n $imagePathMerchant \n $accountSenderName \n ${merhchantNameText.text.toString()} \n ${accountSenderType.text.toString()} \n $nmid \n $terminalId")
            findNavController().navigate(action)
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