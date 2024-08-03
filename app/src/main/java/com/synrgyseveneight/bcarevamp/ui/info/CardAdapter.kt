package com.synrgyseveneight.bcarevamp.ui.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.synrgyseveneight.bcarevamp.R
import com.synrgyseveneight.bcarevamp.databinding.ItemInfooptionsBinding

class CardAdapter(
    private val infoOptions: List<CardAdapter.CardInfo>,
    private val onItemClicked: (CardAdapter.CardInfo) -> Unit
) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    data class CardInfo(
        val title: String,
        val description: String
    )

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.info_options_title)
        val description: TextView = itemView.findViewById(R.id.info_options_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_infooptions, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val infooption = infoOptions[position]
        holder.title.text = infooption.title
        holder.description.text = infooption.description

        // Navigasi
        holder.itemView.setOnClickListener{
            onItemClicked(infooption)
        }
    }

    override fun getItemCount() = infoOptions.size

    // Adapter navigasi ke screen lain
    inner class InfoOptionViewHolder(private val binding: ItemInfooptionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(infoOption: CardInfo) {
            binding.apply {
                // Bind your data to views here
                infoOptionsTitle.text = infoOption.title
                infoOptionsDesc.text = infoOption.description

                root.setOnClickListener {
                    onItemClicked(infoOption)
                }
            }
        }
    }
}

// Adapter navigasi ke screen lain
data class InfoOption(
    val title: String,
    val description: String
)
