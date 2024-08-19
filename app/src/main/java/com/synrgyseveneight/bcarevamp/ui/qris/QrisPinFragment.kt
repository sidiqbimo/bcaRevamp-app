package com.synrgyseveneight.bcarevamp.ui.qris

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.viewmodel.QRISViewModel
import kotlinx.coroutines.launch

class QrisPinFragment : Fragment() {

    private val args: QRISCameraFragmentArgs by navArgs()
    private val viewModel: QRISViewModel by viewModels()
    private lateinit var viewModelQris: QRISViewModel

    private var attemptCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qris_pin, container, false)

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitButton = view.findViewById<Button>(R.id.submitButton)
        val pinEditText = view.findViewById<EditText>(R.id.pinEditText)

        submitButton.setOnClickListener {
            val mpin = pinEditText.text.toString()
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
                    } else {
                        Log.d("QRISPinFragment", "API Call Failed: ${response?.errorBody()?.string()}")
                        val action = QrisPinFragmentDirections.actionQrisPinFragmentToQRISFailedFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }


}