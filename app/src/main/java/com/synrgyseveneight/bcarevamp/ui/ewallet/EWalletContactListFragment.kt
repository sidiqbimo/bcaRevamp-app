package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class EWalletContactListFragment : Fragment() {

    private lateinit var favouriteContactAdapter: ContactListAdapter
    private lateinit var normalContactAdapter: ContactListAdapter

    private val favouriteContacts = listOf(
        ContactListAdapter.Contact(R.drawable.icon_person, "Fav Budi Arto", "Tahapan BCA", "1234567890"),
        ContactListAdapter.Contact(R.drawable.icon_person, "Fav Andi Yassar", "Tahapan BCA", "1234567890")
    )

    private val normalContacts = listOf(
        ContactListAdapter.Contact(R.drawable.icon_person, "Budi Arto", "Tahapan BCA", "1234567890"),
        ContactListAdapter.Contact(R.drawable.icon_person, "Andi Yassar", "Tahapan BCA", "1234567890"),
        ContactListAdapter.Contact(R.drawable.icon_person, "Caca Gempita", "Tahapan BCA", "1234567890"),
        ContactListAdapter.Contact(R.drawable.icon_person, "Dedi Kurniawan", "Tahapan", "1234567890")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle arguments if any
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_e_wallet_contact_list, container, false)

        // Initialize RecyclerView for favourite contacts
        val favouriteRecyclerView = view.findViewById<RecyclerView>(R.id.favouritecontact_recyclerview_tf)
        favouriteRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favouriteContactAdapter = ContactListAdapter(favouriteContacts)
        favouriteRecyclerView.adapter = favouriteContactAdapter

        // Initialize RecyclerView for normal contacts
        val normalRecyclerView = view.findViewById<RecyclerView>(R.id.normalcontact_recyclerview_tf)
        normalRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        normalContactAdapter = ContactListAdapter(normalContacts)
        normalRecyclerView.adapter = normalContactAdapter

        // Back button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        // Transfer to new account button
        val buttonNewAccount = view.findViewById<Button>(R.id.button_newaccount)
        buttonNewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_eWalletContactListFragment_to_eWalletNewInputFragment)
        }

        // Search functionality (simplified)
        val searchEditText = view.findViewById<EditText>(R.id.tf_inputtext_listsearch)
        searchEditText.addTextChangedListener {
            val query = it.toString()
            filterContacts(query)
        }

        return view
    }

    private fun filterContacts(query: String) {
        // Implement search filtering for both adapters
        favouriteContactAdapter.filter(query)
        normalContactAdapter.filter(query)
    }
}
