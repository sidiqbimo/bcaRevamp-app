package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.synrgyseveneight.bcarevamp.viewmodel.TransferViewModel


class TransferConfirmationFragment : Fragment() {
    private val viewModelAuth: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(RetrofitClient.instance, AuthDataStore.getInstance(requireContext())))
    }

    private lateinit var viewModelTransfer : TransferViewModel
    private lateinit var viewModelTransferCheckSender : TransferViewModel
    private val args: TransferConfirmationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_confirmation, container, false)

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accountNumberTarget = args.accountNumberTargetTransfer
        val accountNumberSender = args.accountNumberSenderTransfer
        val transferAmount = args.amountTransfer
        val transferNote = args.noteTransfer
        val myTokenTransfer = args.mytokenTransfer

        // Components  to be change
        val senderAccountDetails = view.findViewById<TextView>(R.id.tvSourceAccountDetails)
        val receiverAccountDetails = view.findViewById<TextView>(R.id.transferDestinationDetail)
        val senderAccountName = view.findViewById<TextView>(R.id.tvUserAccountDetails)
        val receiverAccountName = view.findViewById<TextView>(R.id.tvReceiverName)
        var senderAccountPhotoPath = view.findViewById<ImageView>(R.id.circularImageViewSender)
        var receiverAccountPhotoPath = view.findViewById<ImageView>(R.id.circularImageViewReceiver)
        val transferAmountText = view.findViewById<TextView>(R.id.tvAmountDetails)
        val totalTransferFee = view.findViewById<TextView>(R.id.tvTotalDetails)
        val transferNotes =  view.findViewById<TextView>(R.id.tvMessageDetails)
        val bankTypeTarget = view.findViewById<TextView>(R.id.bankTypeTarget)

        var senderBankType : String = ""
        var receiverBankType : String = ""

        var imagePathSender : String = ""
        var imagePathReceiver : String = ""

        Log.d("TransferConfirmation", "ALL DETAILS - $accountNumberTarget, $accountNumberSender, $transferAmount, $transferNote")

        viewModelTransfer = ViewModelProvider(this).get(TransferViewModel::class.java)
        viewModelTransferCheckSender = ViewModelProvider(this).get(TransferViewModel::class.java)

        // Get token
        viewModelAuth.userToken.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModelAuth.fetchBalance(token)
            }
        }

        viewModelTransfer.accountData.observe(viewLifecycleOwner, Observer { accountData ->
            if (accountData != null) {
                receiverAccountName.text = accountData.name
                imagePathReceiver = accountData.image_path
                bankTypeTarget.text = accountData.bank
                receiverBankType = accountData.bank

               Log.d("TransferConfirmation", "All data get to the LIINE 98 : $accountData")
                Log.d("TransferConfirmation", "Image  path  : $imagePathReceiver")

                Glide.with(this)
                    .load(imagePathReceiver)
                    .circleCrop()
                    .error(R.drawable.icon_person)
                    .into(receiverAccountPhotoPath)
            }
        })



        val token = myTokenTransfer
        if (token != null) {
            // Check receiver account
            viewModelTransfer.searchAccount(token,accountNumberTarget)

            // TODO : Check sender account, use not viewModelTransfer
            viewModelTransferCheckSender.searchSenderAccount(token,accountNumberSender.replace("-", ""))
        } else {
            Toast.makeText(context, "Gagal memuat. Silakan coba lagi", Toast.LENGTH_SHORT).show()
            Log.d("TransferConfirmation", "Token is $token")
            findNavController().navigate(R.id.action_transferConfirmationFragment_to_homeFragment)
        }

        viewModelTransferCheckSender.senderAccountData.observe(viewLifecycleOwner, Observer { senderAccountData ->
            if (senderAccountData != null) {
                senderAccountName.text = senderAccountData.name
                imagePathSender = senderAccountData.image_path
                senderBankType = senderAccountData.bank

                Log.d("TransferConfirmation", "All data get to the LIINE 111 or SENDER : $senderAccountData")
                Log.d("TransferConfirmation", "Image  path : $imagePathSender")

                Glide.with(this)
                    .load(imagePathSender)
                    .circleCrop()
                    .error(R.drawable.icon_person)
                    .into(senderAccountPhotoPath)
            }
        })


        // Error handling
        viewModelTransfer.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })
        viewModelTransferCheckSender.error.observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        // Sender details
        senderAccountDetails.text = "${senderBankType} - ${accountNumberSender.replace("-", "")}"

        // Receiver details
        receiverAccountDetails.text = " - ${accountNumberTarget}"

        // Calculate transfer
        transferAmountText.text = "Rp ${transferAmount}"

        // Total transfer
        totalTransferFee.text = "Rp ${transferAmount}"

        // Fetch notes, if no notes are inputted then it will put dash
        if (transferNote.isEmpty()) {
            transferNotes.text = "-"
        } else {
            transferNotes.text = transferNote
        }

        // Confirm transfer
        val nextButton = view.findViewById<Button>(R.id.btnStartTransferConfirmation)

        nextButton.setOnClickListener {

            // TODO : Save to daftar tersimpan

            // Navigate to next screen tp bawa value
            val action = TransferConfirmationFragmentDirections.actionTransferConfirmationFragmentToTransferPINFragment(
                accountNumberTarget,
                transferAmount.replace(".",""),
                transferNote,
                accountNumberSender.replace("-", ""),
                token,
                imagePathSender,
                imagePathReceiver,
                senderAccountName.text.toString(),
                receiverAccountName.text.toString(),
                senderBankType,
                receiverBankType
            )
            Log.d("TransferSuccessFragment", "Account Number Target: $accountNumberTarget bank of $receiverBankType")
            findNavController().navigate(action)
        }
    }

}