package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.databinding.FragmentEWalletSuccessBinding

class EWalletSuccessFragment : Fragment() {

    private var _binding: FragmentEWalletSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using View Binding
        _binding = FragmentEWalletSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set initial data or fetch data from arguments
        setupInitialData()

        // Set click listeners
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_eWalletSuccessFragment_to_homeFragment)
        }

        binding.buttonShare.setOnClickListener {
            shareTransactionDetails()
        }
    }

    private fun setupInitialData() {
        // Set transaction details, this can be retrieved from arguments or a ViewModel
        binding.tvUserAccountDetails.text = "John Doe"
        binding.tvSourceAccountDetails.text = "TAHAPAN BCA - 1234567890"
        binding.tvReceiverName.text = "Anonim"
        binding.tvEWalletDestinationDetails.text = "E-Wallet 1 - 0987654321"
        binding.tvAmountDetails.text = "Rp 1.000.000"
        binding.tvAdminFeeDetails.text = "Rp 2.500"
        binding.tvMessageDetails.text = "Pembayaran Tagihan"
        binding.tvTotalDetails.text = "Rp 1.002.500"
    }

    private fun shareTransactionDetails() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Bukti Transaksi")
            putExtra(Intent.EXTRA_TEXT, generateTransactionDetails())
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan bukti transaksi"))
    }

    private fun generateTransactionDetails(): String {
        // Create transaction details string for sharing
        return "Transaksi Berhasil!\n" +
                "Pengirim: ${binding.tvUserAccountDetails.text}\n" +
                "Rekening: ${binding.tvSourceAccountDetails.text}\n" +
                "Penerima: ${binding.tvReceiverName.text}\n" +
                "Tujuan: ${binding.tvEWalletDestinationDetails.text}\n" +
                "Nominal Transfer: ${binding.tvAmountDetails.text}\n" +
                "Biaya Admin: ${binding.tvAdminFeeDetails.text}\n" +
                "Catatan: ${binding.tvMessageDetails.text}\n" +
                "TOTAL: ${binding.tvTotalDetails.text}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
