package com.synrgyseveneight.bcarevamp.ui.transfer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R

class TransferListAdapter(private val transferList: List<TransferList>) :
    RecyclerView.Adapter<TransferListAdapter.ViewHolder>() {

    data class TransferList(val iconResId: Int, val accountName: String, val accountTypeTahapan: String, val accountNumber: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val accountphoto: ImageView = itemView.findViewById(R.id.account_photo)
        val accountName: TextView = itemView.findViewById(R.id.accountholdername_title)
        val accountTypeTahapan: TextView = itemView.findViewById(R.id.accounttype)
        val accountNumber: TextView = itemView.findViewById(R.id.accountnumbersubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transfercontact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transfer = transferList[position]
        holder.accountphoto.setImageResource(transfer.iconResId)
        holder.accountName.text = transfer.accountName
        holder.accountTypeTahapan.text = transfer.accountTypeTahapan
        holder.accountNumber.text = transfer.accountNumber
    }

    override fun getItemCount(): Int = transferList.size
}