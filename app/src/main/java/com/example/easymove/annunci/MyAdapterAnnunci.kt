package com.example.easymove.annunci

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.easymove.R
import com.example.easymove.model.Annuncio

class MyAdapterAnnunci(private val list:ArrayList<Annuncio>):RecyclerView.Adapter<MyAdapterAnnunci.MyViewHolder>() {
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val modello: TextView= itemView.findViewById(R.id.modello)
        val targa: TextView= itemView.findViewById(R.id.targa)
        val capienza: TextView= itemView.findViewById(R.id.capienza)
        val locazione: TextView = itemView.findViewById(R.id.locazione)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.singolo_annuncio, parent, false)
    return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
    return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.modello.text=list[position].Modello
        holder.targa.text=list[position].Targa
        holder.capienza.text=list[position].Capienza
        holder.locazione.text=list[position].IndirizzoCompleto

    }
}