package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.ui.home.FavoriteTransactionAdapter

class TransferOptionFragment : Fragment() {

    private lateinit var transferOptionAdapter: TransferOptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_option, container, false)

        // Sample data
        val transferoptions = listOf(
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfantarbca, "Transfer Antar BCA", "Transfer dana ke sesama rekening BCA"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfbanklain, "Transfer ke Bank Lain", "Transfer dana ke bank lain dalam negeri"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_proxytf, "Transfer Proxy Adress", "Transfer dana melalui Proxy Adress"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tf_va, "Virtual Account", "Transfer dana ke rekening Virtual Account BCA"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_tfsakuku, "Sakuku", "Transfer dana ke nomor Sakuku"),
            TransferOptionAdapter.TransferOption(R.drawable.illust_impordaftartf, "Impor Daftar Transfer", "Transfer dana ke nomor Sakuku"),
        )

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.transferOptions_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        transferOptionAdapter = TransferOptionAdapter(transferoptions)  { transferOption ->
            when (transferOption.title) {
                "Transfer Antar BCA" -> findNavController().navigate(R.id.action_transferOptionFragment_to_transferListFragment)
                // Add other navigation actions based on the title
            }}
        recyclerView.adapter = transferOptionAdapter

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }


}