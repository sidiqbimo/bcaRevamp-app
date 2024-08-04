package com.synrgyseveneight.bcarevamp.ui.ewallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class ContactListAdapter(private var contacts: List<Contact>) : RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>() {

    private var filteredContacts: List<Contact> = contacts

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transfercontact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = filteredContacts[position]
        holder.contactName.text = contact.name
        holder.contactBank.text = contact.bank
        holder.contactNumber.text = contact.number
        holder.contactImage.setImageResource(contact.imageRes)
    }

    override fun getItemCount(): Int {
        return filteredContacts.size
    }

    fun filter(query: String) {
        filteredContacts = if (query.isEmpty()) {
            contacts
        } else {
            contacts.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.account_photo)
        val contactName: TextView = itemView.findViewById(R.id.accountholdername_title)
        val contactBank: TextView = itemView.findViewById(R.id.accounttype)
        val contactNumber: TextView = itemView.findViewById(R.id.accountnumbersubtitle)
    }

    data class Contact(
        val imageRes: Int,
        val name: String,
        val bank: String,
        val number: String
    )
}
