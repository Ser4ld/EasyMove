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


class MyAdapterRecensioni(private val recensioniList:ArrayList<Recensione>, private var userList: List<User>, private var userType: String):RecyclerView.Adapter<MyAdapterRecensioni.MyViewHolder>() {

    // Funzione che permette di aggiornare la lista delle recensioni dell'adapter con nuovi dati
    fun updateRecensioni(newDataList: ArrayList<Recensione>) {
        // Cancella la lista esistente e aggiunge tutti gli elementi della nuova lista
        recensioniList.clear()
        recensioniList.addAll(newDataList)

        // Notifica all'adapter che i dati sono cambiati, richiedendo un aggiornamento della RecyclerView
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // ViewHolder che contiene i riferimenti agli elementi UI all'interno di ciascuna card di recensione
        val stelle: RatingBar = itemView.findViewById(R.id.ratingBarRecensione)
        val descrizione: TextView = itemView.findViewById(R.id.textDescrizioneRecensione)
        val nomeCreatore: TextView = itemView.findViewById(R.id.autoreTextView)
        val imgCreatore: ImageView = itemView.findViewById(R.id.profileImageView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Crea e restituisce una nuova istanza di MyViewHolder quando necessario
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_recensione, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        // Restituisce il numero totale di elementi nella lista di recensioni
        return recensioniList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Collega i dati relativi a una specifica posizione
        // nella lista (recensioniList) ai widget UI presenti nel ViewHolder
        val recensione = recensioniList[position]

        val user: User

        // Imposta i dati relativi alla recensione attuale nei widget UI del ViewHolder
        holder.stelle.rating=recensione.valutazione.toFloat()
        holder.descrizione.text=recensione.descrizione

        // Verifica il tipo di utente (guidatore o consumatore)
        if(userType=="guidatore") {
            // se l'utente è guidatore andiamo a memorizzare l'utente consumatore che ha lasciato la recensione
            user = userList.find { it.id == recensione.consumatoreId }!!
        }else{
            // se l'utente è consumatore andiamo a memorizzare l'utente guidatore che ha ricevuto la recensione
            user = userList.find { it.id == recensione.guidatoreId}!!
        }

        // Verifica che l'utente sia presente e imposta i dati relativi all'utente nei widget UI
        if (user != null) {
            holder.nomeCreatore.text = user.name + " " + user.surname

            // Verifica che l'utente abbia un immagine profilo
            if (!user.imageUrl.isNullOrEmpty()) {
                // Carica l'immagine del profilo dell'utente (se disponibile) utilizzando Glide
                Glide.with(holder.itemView.context)
                    .load(user.imageUrl)
                    .circleCrop()
                    .into(holder.imgCreatore)
            } else {
                // Carica un'immagine di default nel caso in cui l'utente non abbia un'immagine del profilo
                holder.imgCreatore.setImageResource(R.drawable.baseline_image_24)
            }
        }
    }
}