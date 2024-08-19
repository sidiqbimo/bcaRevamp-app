package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.R.id.action_transferSuccessFragment_to_homeFragment
import java.text.NumberFormat
import java.util.Locale

class TransferSuccessFragment : Fragment() {

    private val args: TransferPINFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_success, container, false)

        val exitButton = view.findViewById<ImageView>(R.id.btnBack)
        exitButton.setOnClickListener {
            findNavController().navigate(action_transferSuccessFragment_to_homeFragment)
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
        val senderAvatarPath = args.avatarSenderPath
        val receiverAvatarPath = args.avatarTargetPath
        val senderName = args.senderName
        val receiverName = args.receiverName
        val bankSender = args.bankSender
        val bankReceiver = args.bankReceiver
        Log.d("TransferSuccessFragment", "Account Number Target: $accountNumberTarget bank of $bankReceiver")

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
        val bankTypeSender = view.findViewById<TextView>(R.id.bankTypeSource)

        senderAccountDetails.text = " - $accountNumberSender"
        receiverAccountDetails.text = " - $accountNumberTarget"
        senderAccountName.text = senderName
        receiverAccountName.text = receiverName

        val amountTFInt  = transferAmount.replace(".","").toInt()
        val formattedTFAmount = NumberFormat.getNumberInstance(Locale.GERMANY).format(amountTFInt)
        transferAmountText.text = "Rp $formattedTFAmount"

        // Total Fee
        totalTransferFee.text = "Rp $formattedTFAmount"

        if (transferNote.isEmpty()) {
            transferNotes.text = "-"
        } else {
            transferNotes.text = transferNote
        }
        bankTypeTarget.text = bankReceiver
        bankTypeSender.text = bankSender

        // Photo change
        Glide.with(this)
            .load(senderAvatarPath)
            .circleCrop()
            .placeholder(R.drawable.icon_person)
            .error(R.drawable.icon_person)
            .into(senderAccountPhotoPath)
        Glide.with(this)
            .load(receiverAvatarPath)
            .circleCrop()
            .placeholder(R.drawable.icon_person)
            .error(R.drawable.icon_person)
            .into(receiverAccountPhotoPath)

        // Back will go home
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(action_transferSuccessFragment_to_homeFragment)
        }
    }

}