package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.model.MutationResponseData
import java.text.NumberFormat
import java.util.Locale

class MutationProofAdapter(private var mutationProofItems: List<MutationResponseData>) :
    RecyclerView.Adapter<MutationProofAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.text_date)
        val textReff: TextView = itemView.findViewById(R.id.text_reff)
        val textType: TextView = itemView.findViewById(R.id.text_type)
        val textStatus: TextView = itemView.findViewById(R.id.text_status)
        val textNumber: TextView = itemView.findViewById(R.id.text_id)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
        val spaceIdNominal: Space = itemView.findViewById(R.id.space_id_nominal)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationproof, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationProofItems[position]

        holder.textDate.text = item.formatted_date
        holder.textReff.text = "No Reff : "+ item.reference_number
        holder.textType.text = item.type
        holder.textStatus.text = "Sukses"
        holder.textNominal.text = formatBalance(item.total_amount.toDouble())
        if (item.destination_account_number == null && item.destination_phone_number == null) {
            holder.textNumber.visibility = View.GONE
            holder.spaceIdNominal.visibility = View.GONE
        } else if (item.destination_phone_number == null) {
            holder.textNumber.text = item.destination_account_number
        } else if (item.destination_account_number == null) {
            holder.textNumber.text = item.destination_phone_number
        }
    }

    override fun getItemCount(): Int = mutationProofItems.size

    fun updateData(newItems: List<MutationResponseData>) {
        mutationProofItems = newItems.sortedByDescending { it.formatted_date }
        notifyDataSetChanged()
    }

    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return "Rp " + formatter.format(balance)
    }
}