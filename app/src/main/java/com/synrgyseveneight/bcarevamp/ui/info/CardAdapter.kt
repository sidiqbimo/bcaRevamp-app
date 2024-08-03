package com.synrgyseveneight.bcarevamp.ui.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(
    private val cardList: List<CardInfo>,
    private val onArrowClick: (CardInfo)-> Unit
) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    data class CardInfo(
        val title: String,
        val description: String,
        val arrow: Int
    )

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(androidx.recyclerview.R.id.title)
        val description: TextView = itemView.findViewById(com.google.android.material.R.id.tag_state_description)
        val arrow: ImageView = itemView.findViewById(R.id.arrowRightButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_info_menu, parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = cardList[position]
        holder.title.text = currentItem.title
        holder.description.text = currentItem.description
        holder.arrow.setOnClickListener{
            onArrowClick(currentItem)
        }
    }

    override fun getItemCount() = cardList.size
}

