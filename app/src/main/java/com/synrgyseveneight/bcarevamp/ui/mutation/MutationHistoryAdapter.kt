package com.synrgyseveneight.bcarevamp.ui.mutation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.data.model.MutationResponseData
import java.text.NumberFormat
import java.util.Locale

class MutationHistoryAdapter(private var mutationHistoryItems: List<MutationResponseData>) :
    RecyclerView.Adapter<MutationHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.text_id)
        val textType: TextView = itemView.findViewById(R.id.text_type)
        val textNominal: TextView = itemView.findViewById(R.id.text_nominal)
        val textTime: TextView = itemView.findViewById(R.id.text_time)
        val textDate: TextView = itemView.findViewById(R.id.text_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mutationhistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mutationHistoryItems[position]
        holder.textId.text = item.unique_code
        holder.textTime.text = item.formatted_time
        holder.textType.text = item.type


        if (item.type.equals("DEPOSIT", ignoreCase = true)) {
            holder.textNominal.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.teal_700))
            holder.textNominal.text = "+ " + formatBalance(item.total_amount.toDouble())
        } else {
            holder.textNominal.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.Error0))
            holder.textNominal.text = "- " + formatBalance(item.total_amount.toDouble())
        }

        if (position == 0 || item.formatted_date != mutationHistoryItems[position - 1].formatted_date) {
            holder.textDate.visibility = View.VISIBLE
            holder.textDate.text = item.formatted_date
        } else {
            holder.textDate.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = mutationHistoryItems.size

    fun updateData(newItems: List<MutationResponseData>) {
        mutationHistoryItems = newItems.sortedByDescending { it.time}
        notifyDataSetChanged()
    }

    private fun formatBalance(balance: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.GERMANY)
        return "Rp " + formatter.format(balance)
    }
}
