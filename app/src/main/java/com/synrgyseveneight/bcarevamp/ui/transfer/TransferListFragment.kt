package com.synrgyseveneight.bcarevamp.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class TransferListFragment : Fragment() {

    private lateinit var transferListAdapter: TransferListAdapter
    private lateinit var favTransferListAdapter: TransferFavouriteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transfer_list, container, false)

        // Sample data untuk kontak tersimpan NON-favorit
        val transferList = listOf(
            TransferListAdapter.TransferList(R.drawable.icon_person, "Mama BCA", "Tahapan", "1234567890"),
            TransferListAdapter.TransferList(R.drawable.icon_person, "Nana BCA", "Tahapan", "1234567890"),
            TransferListAdapter.TransferList(R.drawable.icon_person, "Oma BCA", "Tahapan", "1234567890"),
            TransferListAdapter.TransferList(R.drawable.icon_person, "Papa BCA", "Tahapan", "1234567890"),
        )
        val favTransferList = listOf(
            TransferFavouriteListAdapter.TransferList(R.drawable.icon_person, "Danang Edward", "Tahapan", "1234567890"),
            TransferFavouriteListAdapter.TransferList(R.drawable.icon_person, "Heru Darmawan", "Tahapan", "1234567890"),
        )

        // Initialize RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.normalcontact_recyclerview_tf)
        recyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        transferListAdapter = TransferListAdapter(transferList)
        recyclerView.adapter = transferListAdapter

        val recyclerViewFavourtied = view.findViewById<RecyclerView>(R.id.favouritecontact_recyclerview_tf)
        recyclerViewFavourtied.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        favTransferListAdapter = TransferFavouriteListAdapter(favTransferList)
        recyclerViewFavourtied.adapter = favTransferListAdapter

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // Transfer to new account button
        val button_newaccount = view.findViewById<Button>(R.id.button_newaccount)
        button_newaccount.setOnClickListener {
            findNavController().navigate(R.id.action_transferListFragment_to_newAccountTransferInput)
        }

        return view
    }

//    TODO: Scroll masih ganda - scrollview dan recyclerview; Fitur favorit belum bisa; overflow di bagian Papa BCA; search belum bisa
}