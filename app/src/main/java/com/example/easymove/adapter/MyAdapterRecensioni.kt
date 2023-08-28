package com.example.easymove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.model.Recensione
import com.example.easymove.model.User


class MyAdapterRecensioni(private val recensioniList:ArrayList<Recensione>, private var userList: List<User>):RecyclerView.Adapter<MyAdapterRecensioni.MyViewHolder>() {

    fun updateRecensioni(newDataList: ArrayList<Recensione>) {
        recensioniList.clear()
        recensioniList.addAll(newDataList)
        notifyDataSetChanged()
    }
    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val stelle: RatingBar = itemView.findViewById(R.id.ratingBarRecensione)
        val descrizione: TextView = itemView.findViewById(R.id.textDescrizioneRecensione)
        val nomeCreatore: TextView = itemView.findViewById(R.id.autoreTextView)
        val imgCreatore: ImageView = itemView.findViewById(R.id.profileImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_recensione, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recensioniList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.stelle.rating=recensioniList[position].stelline.toFloat()
        holder.descrizione.text=recensioniList[position].descrizione

        val recensione = recensioniList[position]

        val user = userList.find { it.id == recensione.idCreatore}

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