package com.example.easymove.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.model.Richiesta
import com.example.easymove.model.User
import kotlin.properties.Delegates

class MyAdapterRichieste(private val richiesteList: ArrayList<Richiesta>, private var userList: List<User>,private val richiestaViewModel: RichiestaViewModel) : RecyclerView.Adapter<MyAdapterRichieste.MyViewHolder>() {

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
        val button1: Button = itemView.findViewById(R.id.button1)
        val button2: Button = itemView.findViewById(R.id.button2)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_richiesta, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return richiesteList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var richiesta = richiesteList[position]
        var stato=richiesta.stato


        holder.descrizione.text = richiesta.descrizione
        holder.nomeCreatore.text = getUserFullName(richiesta.guidatoreId)
        holder.statoRichiesta.text = stato


        updateUI(richiesta, holder, stato)

        val user = userList.find { it.id == richiesta.guidatoreId }
        if (user != null) {
            caricaDettagliUtente(holder, user)
        }
    }

    private fun onButtonAccettaClicked(richiesta: Richiesta, nuovoStato:String) {
        val richiestaId = richiesta.richiestaId

        richiestaViewModel.updateRichiestaStato(richiestaId, nuovoStato) { success, errMsg ->
            if (success) {
                Log.d("provastato", "Stato aggiornato con successo: $nuovoStato")
            } else {
                Log.d("provastato", "Errore nell'aggiornamento dello stato: $errMsg")
            }
        }
    }

    private fun getUserFullName(userId: String): String {
        val user = userList.find { it.id == userId }
        return user?.let { "${it.name} ${it.surname}" } ?: ""
    }

    private fun caricaDettagliUtente(holder: MyViewHolder, user: User) {
        holder.nomeCreatore.text = "${user.name} ${user.surname}"

        if (!user.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(user.imageUrl)
                .circleCrop()
                .into(holder.imgCreatore)
        } else {
            holder.imgCreatore.setImageResource(R.drawable.baseline_image_24)
        }
    }


    private fun updateUI(richiesta:Richiesta, holder: MyViewHolder, stato: String) {
        val context = holder.itemView.context
        val button1=holder.button1
        val button2=holder.button2


        var coloreStato: Int
        when (stato) {
            "Attesa" -> {
                button1.text = "ACCETTA"
                button2.text = "RIFIUTA"

                coloreStato = ContextCompat.getColor(context, R.color.selected_star_color)
                holder.statoRichiesta.setTextColor(coloreStato)

                holder.button1.setOnClickListener {
                    onButtonAccettaClicked(richiesta, "Accettata")
                }
                holder.button2.setOnClickListener {
                    onButtonAccettaClicked(richiesta, "Rifiutata")
                }
            }
            "Accettata" -> {

                button1.text = "COMPLETATA"
                button2.text = "ANNULLATA"
                coloreStato = ContextCompat.getColor(context, R.color.lime_green)
                holder.statoRichiesta.setTextColor(coloreStato)

                holder.button1.setOnClickListener {
                    onButtonAccettaClicked(richiesta, "Rifiutata")
                }
                holder.button2.setOnClickListener {
                    onButtonAccettaClicked(richiesta, "Rifiutata")
                }

            }
            else -> {
                button1.visibility= GONE
                button2.visibility = GONE
                //button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

                coloreStato = ContextCompat.getColor(context, R.color.red)
                holder.statoRichiesta.setTextColor(coloreStato)

            }
        }
    }

}
