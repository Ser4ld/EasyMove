package com.example.easymove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.model.Richiesta
import com.example.easymove.model.User

class MyAdapterRichieste(private val richiesteList: ArrayList<Richiesta>, private var userList: List<User>) : RecyclerView.Adapter<MyAdapterRichieste.MyViewHolder>() {

    // Aggiorna la lista delle richieste con nuovi dati
    fun updateRichieste(newDataList: ArrayList<Richiesta>) {
        richiesteList.clear()
        richiesteList.addAll(newDataList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nomeCreatore: TextView= itemView.findViewById(R.id.autoreTextView)
        val imgCreatore: ImageView = itemView.findViewById(R.id.profileImageView)
        val descrizione: TextView=itemView.findViewById(R.id.textDescrizioneRichiesta)
        val statoRichiesta: TextView=itemView.findViewById(R.id.textStatoRichiesta)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_richiesta, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return richiesteList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val richiesta = richiesteList[position]

        holder.descrizione.text= richiesta.descrizione
        holder.nomeCreatore.text= richiesta.consumatoreId
        holder.statoRichiesta.text= richiesta.stato

        if (richiesteList[position].stato == "inAttesa") {
            holder.statoRichiesta.setTextColor(holder.itemView.context.getColor(R.color.selected_star_color))
        }
        val user = userList.find { it.id == richiesta.guidatoreId}

        if (user != null) {
            holder.nomeCreatore.text = user.name + " " + user.surname

            if (!user.imageUrl.isNullOrEmpty()) {
                // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
                Glide.with(holder.itemView.context)
                    .load(user.imageUrl)
                    .circleCrop()
                    .into(holder.imgCreatore)
            } else {
                // Carica l'immagine di default
                holder.imgCreatore.setImageResource(R.drawable.baseline_image_24)
            }
        }
    }
}
