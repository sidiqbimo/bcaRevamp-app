package com.synrgyseveneight.bcarevamp.ui.transfer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.ui.home.FavoriteTransactionAdapter
import com.synrgyseveneight.bcarevamp.databinding.ItemTransferoptionsBinding

class TransferOptionAdapter(private val transferoptions: List<TransferOptionAdapter.TransferOption>,private val onItemClicked : (TransferOptionAdapter.TransferOption) -> Unit) :
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

        holder.itemView.setOnClickListener {
            onItemClicked(transferoption)
        }
    }

    override fun getItemCount(): Int = transferoptions.size

    inner class TransferOptionViewHolder(private val binding: ItemTransferoptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transferOption: TransferOption) {
            binding.apply {
                // Bind your data to views here
                tfOptionsTitle.text = transferOption.title
                tfOptionsIcon.setImageResource(transferOption.iconResId)

                root.setOnClickListener {
                    onItemClicked(transferOption)
                }
            }
        }
    }
}

data class TransferOption(
    val title: String,
    val iconResId: Int
)