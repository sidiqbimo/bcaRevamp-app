package com.synrgyseveneight.bcarevamp.ui.transfer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.ui.home.FavoriteTransactionAdapter

class TransferOptionAdapter(private val transferoptions: List<TransferOptionAdapter.TransferOption>) :
    RecyclerView.Adapter<TransferOptionAdapter.ViewHolder>() {

    data class TransferOption(val iconResId: Int, val title: String, val subtitle: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.tf_options_icon)
        val title: TextView = itemView.findViewById(R.id.tf_options_title)
        val subtitle: TextView = itemView.findViewById(R.id.tf_options_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transferoptions, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transferoption = transferoptions[position]
        holder.icon.setImageResource(transferoption.iconResId)
        holder.title.text = transferoption.title
        holder.subtitle.text = transferoption.subtitle
    }

    override fun getItemCount(): Int = transferoptions.size
}