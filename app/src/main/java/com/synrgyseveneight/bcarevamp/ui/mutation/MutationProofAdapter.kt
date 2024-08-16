package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.model.MutationData

class MutationProofAdapter(private var mutationProofItems: List<MutationData>) :
    RecyclerView.Adapter<MutationProofAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val textReff: TextView = itemView.findViewById(R.id.text_reff)
        val textType: TextView = itemView.findViewById(R.id.text_type)
        val textStatus: TextView = itemView.findViewById(R.id.text_status)
        val textId: TextView = itemView.findViewById(R.id.text_id)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationproof, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationProofItems[position]
        holder.textDate.text = item.formatted_date
        holder.textReff.text = item.reference_number
        holder.textType.text = item.type
        holder.textStatus.text = "Sukses"
        holder.textId.text = item.transaction_id
        holder.textNominal.text = item.total_amount.toString()
    }

    override fun getItemCount(): Int = mutationProofItems.size

    fun updateData(newItems: List<MutationData>) {
        mutationProofItems = newItems
        notifyDataSetChanged()
    }
}