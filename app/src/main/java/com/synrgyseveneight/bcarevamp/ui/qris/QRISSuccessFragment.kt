package com.synrgyseveneight.bcarevamp.ui.qris

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
import java.text.NumberFormat
import java.util.Locale

class QRISSuccessFragment : Fragment() {


    private val args: QRISCameraFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_q_r_i_s_success, container, false)

        val exitButton = view.findViewById<ImageView>(R.id.btnBack)
        exitButton.setOnClickListener {
            findNavController().apply {
                popBackStack(R.id.homeFragment, false)
                navigate(R.id.homeFragment)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        Log.d("QRISSuccessFragment", "ALL DATA : $idQris, $accountNumberSender, $transferAmount, $transferNote, $myTokenTransfer, $imagePathSender, $imagePathMerchant, $accountSenderName, $merhchantNameText, $accountSenderType, $nmid, $terminalId")

        // components to be changed
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
        receiverAccountDetails.text = " - $nmid"
        senderAccountName.text = accountSenderName
        receiverAccountName.text = merhchantNameText

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
        bankTypeTarget.text = terminalId
        bankTypeSender.text = accountSenderType

        // Photo change
        Glide.with(this)
            .load(imagePathSender)
            .circleCrop()
            .placeholder(R.drawable.icon_person)
            .error(R.drawable.icon_person)
            .into(senderAccountPhotoPath)
        Glide.with(this)
            .load(imagePathMerchant)
            .circleCrop()
            .placeholder(R.drawable.icon_person)
            .error(R.drawable.icon_person)
            .into(receiverAccountPhotoPath)

        // Back will go home
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_QRISSuccessFragment_to_homeFragment)
    }}

}