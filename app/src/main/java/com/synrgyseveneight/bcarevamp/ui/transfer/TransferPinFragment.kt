package com.synrgyseveneight.bcarevamp.ui.transfer

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
import com.synrgyseveneight.bcarevamp.viewmodel.TransferViewModel
import kotlinx.coroutines.launch

class TransferPINFragment : Fragment() {


    private val args: TransferPINFragmentArgs by navArgs()
    private val viewModel: TransferViewModel by viewModels()
    private var attemptCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_transfer_pin, container, false)

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
                        val action = TransferPINFragmentDirections.actionTransferPINFragmentToTransferFailedFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun performTransfer(mpin: String) {
        val accountNumberTarget = args.accountNumberTargetTransfer
        val accountNumberSender = args.accountNumberSenderTransfer
        val transferAmount = args.amountTransfer
        val transferNote = args.noteTransfer
        val myTokenTransfer = args.mytokenTransfer

        lifecycleScope.launch {
            val response = viewModel.performTransfer(
                myTokenTransfer, accountNumberTarget, transferAmount.replace(".","").toInt(), mpin, transferNote, false
            )
            if (response == null) {
                // Navigate back to home screen
                findNavController().navigate(TransferPINFragmentDirections.actionTransferPINFragmentToHomeFragment())
            } else {
                // Handle the response as before
                if (response.isSuccessful && response.body()?.status == true) {
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
                    findNavController().navigate(action)
                } else{
                    val action = TransferPINFragmentDirections.actionTransferPINFragmentToTransferFailedFragment()
                    findNavController().navigate(action)
                }
            }
        }

    }


}
