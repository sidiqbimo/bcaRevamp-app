package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class TransferListFragment : Fragment() {

    private lateinit var transferListAdapter: TransferListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_list, container, false)

        val transferList = listOf(
            TransferListAdapter.TransferList(R.drawable.icon_person, "Budi Arto", "Tahapan BCA", "1234567890"),
            // Add more items here
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.normalcontact_recyclerview_tf)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        transferListAdapter = TransferListAdapter(transferList) { transferListItem ->
            val bundle = Bundle().apply {
                putInt("icon", transferListItem.iconResId)
                putString("name", transferListItem.accountName)
                putString("accountType", transferListItem.accountTypeTahapan)
                putString("accountNumber", transferListItem.accountNumber)
            }
            findNavController().navigate(R.id.action_transferListFragment_to_transferInputFragment, bundle)
        }
        recyclerView.adapter = transferListAdapter

        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val button_newaccount = view.findViewById<Button>(R.id.button_newaccount)
        button_newaccount.setOnClickListener {
            findNavController().navigate(R.id.action_transferListFragment_to_newAccountTransferInput)
        }

        return view
    }
}
