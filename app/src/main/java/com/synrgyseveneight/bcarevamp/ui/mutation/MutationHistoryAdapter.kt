package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.model.MutationData

class MutationHistoryAdapter(private var mutationItems: List<MutationData>) :
    RecyclerView.Adapter<MutationHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.text_id)
        val textType: TextView = itemView.findViewById(R.id.text_type)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
        val textTime: TextView = itemView.findViewById(R.id.text_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationhistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationItems[position]
        holder.textId.text = item.unique_code
        holder.textTime.text = item.formatted_time
        holder.textType.text = item.type

        if (item.type.equals("DEPOSIT", ignoreCase = true)) { // Sesuaikan dengan kondisi Anda
            holder.textNominal.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.teal_700))
            holder.textNominal.text = item.total_amount.toString()
        } else {
            // Kembalikan ke warna default jika bukan "Deposit"
            holder.textNominal.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Error0))
            holder.textNominal.text = item.total_amount.toString()
        }
    }

    override fun getItemCount(): Int = mutationItems.size

    fun updateData(newItems: List<MutationData>) {
        mutationItems = newItems
        notifyDataSetChanged()
    }
}
