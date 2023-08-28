package com.example.easymove.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easymove.R
import com.example.easymove.model.Recensione


class MyAdapterRecensioni (private val list:ArrayList<Recensione>):RecyclerView.Adapter<MyAdapterRecensioni.MyViewHolder>() {
    fun updateRecensioni(newDataList: List<Recensione>) {
        list.clear()
        list.addAll(newDataList)
        notifyDataSetChanged()
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val stelle: RatingBar = itemView.findViewById(R.id.ratingBarRecensione)
        val descrizione: TextView = itemView.findViewById(R.id.textDescrizioneRecensione)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_recensione, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.stelle.rating=list[position].stelline.toFloat()
        holder.descrizione.text=list[position].descrizione
    }


}