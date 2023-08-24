package com.example.easymove.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.View.InoltraRichiestaFragment
import com.example.easymove.View.ListaVeicoliFragment
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.model.Veicolo


class MyAdapterVeicoli(private val veicoliViewModel: VeicoliViewModel,private val list:ArrayList<Veicolo>):RecyclerView.Adapter<MyAdapterVeicoli.MyViewHolder>() {

    init {
        list.sortBy { it.modello }
    }

    fun updateData(newDataList: ArrayList<Veicolo>) {
        list.clear()
        newDataList.sortBy { it.modello }
        list.addAll(newDataList)
        notifyDataSetChanged()
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val modello: TextView= itemView.findViewById(R.id.modello)
        val targa: TextView= itemView.findViewById(R.id.targa)
        val capienza: TextView= itemView.findViewById(R.id.capienza)
        val locazione: TextView = itemView.findViewById(R.id.locazione)
        val annuncioImageView: ImageView = itemView.findViewById(R.id.annuncioImageView)

        val button: Button = itemView.findViewById(R.id.btnRichiediTrasporto)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_singolo_annuncio, parent, false)
    return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
    return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.button.setOnClickListener {
            veicoliViewModel.onVeicoloClicked(position)
        }

        holder.modello.text = list[position].modello
        holder.targa.text = list[position].targa
        holder.capienza.text = list[position].capienza
        holder.locazione.text = list[position].via +" "+list[position].numeroCivico+", "+list[position].citta+", "+list[position].codicePostale

        if (!list[position].imageUrl.isNullOrEmpty()) {

            // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
            Glide.with(holder.itemView.context)
                .load(list[position].imageUrl)
                .into(holder.annuncioImageView)
        } else {
            // Carica l'immagine di default
            holder.annuncioImageView.setImageResource(R.drawable.baseline_image_24)
        }
    }


}