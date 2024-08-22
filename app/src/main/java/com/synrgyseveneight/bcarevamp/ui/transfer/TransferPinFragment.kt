package com.synrgyseveneight.bcarevamp.ui.transfer

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
import com.synrgyseveneight.bcarevamp.viewmodel.TransferViewModel
import kotlinx.coroutines.launch

class TransferPINFragment : Fragment() {


    private val args: TransferPINFragmentArgs by navArgs()
    private val viewModel: TransferViewModel by viewModels()
    private var attemptCount = 3
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private val pinBuilder = StringBuilder()
    private lateinit var pinDots: Array<ImageView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_transfer_pin, container, false)

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

   private fun performTransfer() {
       val mpin = pinBuilder.toString()
       if (mpin.isNotEmpty()) {
           val accountNumberTarget = args.accountNumberTargetTransfer
           val accountNumberSender = args.accountNumberSenderTransfer
           val transferAmount = args.amountTransfer
           val transferNote = args.noteTransfer
           val myTokenTransfer = args.mytokenTransfer

           lifecycleScope.launch {
               val response = viewModel.performTransfer(
                   myTokenTransfer, accountNumberTarget, transferAmount.replace(".","").toInt(), mpin, transferNote,false
               )
               Log.d("TransferPINFragment", "API Response: $response")
               if (response?.isSuccessful == true) {
                   if (response?.code() == 400){
                       handleWrongPin()
                   }
                   val body = response.body()
                   Log.d("TransferPINFragment", "Response Body: $body")
                   if (body?.status == true) {
                       val action = TransferPINFragmentDirections.actionTransferPINFragmentToTransferSuccessFragment(
                           accountNumberTarget,
                           transferAmount.replace(".",""),
                           transferNote,
                           accountNumberSender.replace("-", ""),
                           args.mytokenTransfer,
                           args.avatarSenderPath,
                           args.avatarTargetPath,
                           args.senderName,
                           args.receiverName,
                           args.bankSender,
                           args.bankReceiver
                       )
                       Log.d("TransferSuccessFragment", "Account Number Target: $accountNumberTarget bank of $args.bankReceiver")
                       // TODO : Add Popup to Inclusive
                       findNavController().navigate(action)
                   } else {
                       Log.d("TransferPINFragment", "Transfer Failed: ${body?.message}")
                       val action = TransferPINFragmentDirections.actionTransferPINFragmentToTransferFailedFragment()
                       findNavController().navigate(action)
                   }
               } else {
                   Log.d("TransferPINFragment", "API Call Failed: ${response?.errorBody()?.string()}")
                   if (response?.code() == 400){
                       handleWrongPin()
                   } else {val action = TransferPINFragmentDirections.actionTransferPINFragmentToTransferFailedFragment()
                   findNavController().navigate(action)}
               }
           }
       } else {
           showCustomToast("PIN tidak boleh kosong")
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



}
