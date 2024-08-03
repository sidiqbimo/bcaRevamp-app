package com.synrgyseveneight.bcarevamp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteTransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<FavoriteTransactionAdapter.ViewHolder>() {

    data class Transaction(val iconResId: Int, val title: String, val subtitle: String, val accountName: String)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.fav_tf_icon)
        val title: TextView = itemView.findViewById(R.id.fav_tf_title)
        val subtitle: TextView = itemView.findViewById(R.id.fav_transaction_title)
        val accountName: TextView = itemView.findViewById(R.id.fav_transaction_account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.icon.setImageResource(transaction.iconResId)
        holder.title.text = transaction.title
        holder.subtitle.text = transaction.subtitle
        holder.accountName.text = transaction.accountName
    }

    override fun getItemCount(): Int = transactions.size
}
