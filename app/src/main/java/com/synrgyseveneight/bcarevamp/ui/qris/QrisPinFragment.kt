package com.synrgyseveneight.bcarevamp.ui.qris

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.datastore.AuthDataStore
import com.synrgyseveneight.bcarevamp.data.network.RetrofitClient
import com.synrgyseveneight.bcarevamp.data.repository.AuthRepository
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModel
import com.synrgyseveneight.bcarevamp.viewmodel.AuthViewModelFactory
import com.synrgyseveneight.bcarevamp.viewmodel.QRISViewModel
import kotlinx.coroutines.launch

class QrisPinFragment : Fragment() {

    private val args: QRISCameraFragmentArgs by navArgs()
    private val viewModel: QRISViewModel by viewModels()
    private lateinit var viewModelQris: QRISViewModel
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private val pinBuilder = StringBuilder()
    private lateinit var pinDots: Array<ImageView>

    private var attemptCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qris_pin, container, false)

        pinDots = arrayOf(
            view.findViewById(R.id.pinDot1),
            view.findViewById(R.id.pinDot2),
            view.findViewById(R.id.pinDot3),
            view.findViewById(R.id.pinDot4),
            view.findViewById(R.id.pinDot5),
            view.findViewById(R.id.pinDot6)
        )

        // Setup tombol angka
        setupNumpadButtons(view)

        // Setup tombol hapus
        view.findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            deleteLastDigit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setupNumpadButtons(view: View) {
        val buttonIds = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in buttonIds) {
            view.findViewById<Button>(id).setOnClickListener { button ->
                addDigit((button as Button).text.toString())
            }
        }
    }

    private fun addDigit(digit: String) {
        if (pinBuilder.length < 6) {
            pinBuilder.append(digit)
            updatePinDots()
            if (pinBuilder.length == 6) {
                performTransfer()
            }
        }
    }

    private fun performTransfer () {
        val mpin = pinBuilder.toString()
        Log.d("QRISPinFragment", "MPIN: $mpin")
        if (mpin.isNotEmpty()) {
            val idQris = args.idQris
            val accountNumberSender = args.accountNumberSenderTransfer
            val transferAmount = args.amountTransfer
            val transferNote = args.noteTransfer
            val myTokenTransfer = args.token
            val imagePathSender = args.avatarSenderPath
            val imagePathMerchant = args.avatarTargetPath
            val accountSenderName = args.senderName
            val merhchantNameText = args.receiverName
            val accountSenderType = args.bankSender
            val nmid = args.nmid
            val terminalId = args.terminalid

            Log.d("QRISPinFragment", "ALL DATA TAKEN FROM NOMINAL INPUT : $idQris, $accountNumberSender, $transferAmount, $transferNote, $myTokenTransfer, $imagePathSender, $imagePathMerchant, $accountSenderName, $merhchantNameText, $accountSenderType, $nmid, $terminalId")

            val transferAmountInt:Int = transferAmount.replace(".","").toInt()


            lifecycleScope.launch {
                val response = viewModel.performQrisTransfer(
                    idQris, transferAmountInt, transferNote,mpin,myTokenTransfer
                )
                Log.d("QRISPinFragment", "API Response: $response")
                if (response?.isSuccessful == true) {
                    val body = response.body()
                    Log.d("QRISPinFragment", "Response Body: $body")
                    if (body?.status == true) {
                        if (response?.message() == "Invalid MPIN"){
                            handleWrongPin()
                        }
                        Log.d("QRISPinFragment", "Transfer Success: ${body?.message} with message response ${response.message()}")
                        val action = QrisPinFragmentDirections.actionQrisPinFragmentToQRISSuccessFragment(
                            args.idQris,
                            myTokenTransfer,
                            transferAmountInt.toString(),
                            transferNote,
                            accountNumberSender.toString().replace("-", ""),
                            args.avatarSenderPath,
                            imagePathMerchant,
                            accountSenderName.toString(),
                            merhchantNameText.toString(),
                            accountSenderType.toString(),
                            nmid,
                            terminalId
                        )
                        Log.d("QRISPinFragment", "Account Number Target: $idQris bank of $args.bankReceiver")
                        // TODO : Add Popup to Inclusive
                        findNavController().navigate(action)
                    } else {
                        Log.d("QRISPinFragment", "Transfer Failed: ${body?.message}")
                        val action = QrisPinFragmentDirections.actionQrisPinFragmentToQRISFailedFragment()
                        findNavController().navigate(action)
                    }
                }
                else {
                    Log.d("QRISPinFragment", "Info for API failed\n${response?.errorBody()}\n${response?.code()}\n${response?.message()}")
                    if (response?.code() == 400){
                        handleWrongPin()
                    } else {Log.d("QRISPinFragment", "API Call Failed: ${response?.errorBody()?.string()}")
                    val action = QrisPinFragmentDirections.actionQrisPinFragmentToQRISFailedFragment()
                    findNavController().navigate(action)}
                }
            }
        }
    }

    private fun handleWrongPin() {
        attemptCount--
        pinBuilder.clear()
        updatePinDots()

        if (attemptCount > 0) {
            showCustomToast("PIN salah, Anda mempunyai $attemptCount kali kesempatan lagi.")
        } else {
            showCustomToast("PIN salah")
            viewModelAuth.clearToken {
                findNavController().apply {
                    popBackStack(R.id.loginFragment, false)
                    navigate(R.id.loginFragment)
                }
            }

        }
    }

    @SuppressLint("ResourceAsColor")
    private fun showCustomToast(message: String){

        //create linear layout
        val customToastLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            background = ContextCompat.getDrawable(requireContext(), R.drawable.toast_background)
            elevation = 10f
        }

        val toastIcon = ImageView(requireContext()).apply {
            setImageResource(R.drawable.icon_toast)
            setPadding(0, 0, 16, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                setMargins(24, 0, 24, 0)
            }
        }

        val typefaces = ResourcesCompat.getFont(requireContext(), R.font.nunitoregular)


        //text view for toast message
        val toastTextView = TextView(requireContext()).apply {
            text = message
            setTextColor(R.color.darkBlue)
            textSize = 16f
            typeface = typefaces

        }

        customToastLayout.addView(toastIcon)
        customToastLayout.addView(toastTextView)

        with(Toast(requireContext())){
            duration = Toast.LENGTH_SHORT
            view = customToastLayout
            setGravity(Gravity.CENTER, 0,400)
            show()
        }

        //accessibility
        customToastLayout.announceForAccessibility(message)

    }

    private fun deleteLastDigit() {
        if (pinBuilder.isNotEmpty()) {
            pinBuilder.deleteAt(pinBuilder.length - 1)
            updatePinDots()
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < pinBuilder.length) {
                pinDots[i].setImageResource(R.drawable.pin_active)
            } else {
                pinDots[i].setImageResource(R.drawable.pin_inactive)
            }
        }
    }


}