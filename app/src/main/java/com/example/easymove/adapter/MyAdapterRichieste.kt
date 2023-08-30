package com.example.easymove.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.model.Richiesta
import com.example.easymove.model.User
import com.example.easymove.model.Veicolo
import kotlin.properties.Delegates

class MyAdapterRichieste(
    private val richiesteList: ArrayList<Richiesta>,
    private var veicoliList: List<Veicolo> ,
    private var userList: List<User>,
    private var userType: String,
    private val richiestaViewModel: RichiestaViewModel,
    private val userViewModel: UserViewModel,
    private val veicoliViewModel: VeicoliViewModel) : RecyclerView.Adapter<MyAdapterRichieste.MyViewHolder>() {

    // Aggiorna la lista delle richieste con nuovi dati
    fun updateRichieste(newDataList: ArrayList<Richiesta>) {
        richiesteList.clear()
        richiesteList.addAll(newDataList)
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nomeCreatore: TextView= itemView.findViewById(R.id.autoreTextView)
        val imgCreatore: ImageView = itemView.findViewById(R.id.profileImageView)
        val descrizione: TextView=itemView.findViewById(R.id.textDescrizione)
        val statoRichiesta: TextView=itemView.findViewById(R.id.textStatoRichiesta)
        val dataRichiesta: TextView= itemView.findViewById(R.id.textData)
        val puntoPartenza: TextView= itemView.findViewById(R.id.textPuntoPartenza)
        val puntoArrivo: TextView= itemView.findViewById(R.id.textPuntoArrivo)
        val nomeVeicolo: TextView= itemView.findViewById(R.id.textNomeVeicolo)
        val targaVeicolo: TextView= itemView.findViewById(R.id.textTargaVeicolo)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var richiesta = richiesteList[position]
        var stato=richiesta.stato


        if(userViewModel.checkUserType(userType)){
            val user = userViewModel.FilterListById(richiesta.consumatoreId, userList)
            if (user != null) {
                caricaDettagliUtente(holder, user)
            }

        }else {
            val user = userViewModel.FilterListById(richiesta.guidatoreId, userList)
            if (user != null) {
                caricaDettagliUtente(holder, user)
            }

        }


        val veicolo = veicoliViewModel.FilterListbyTarga(richiesta.targaveicolo, veicoliList)
        if(veicolo != null){
            caricaDettagliVeicolo(holder,veicolo)
        }

        holder.descrizione.text = "Descrizione: ${richiesta.descrizione}"
        holder.statoRichiesta.text = "Stato: $stato"
        holder.dataRichiesta.text = "Data: ${richiesta.data}"
        holder.puntoPartenza.text = "Partenza: ${richiesta.puntoPartenza}"
        holder.puntoArrivo.text = "Arrivo: ${richiesta.puntoArrivo}"
        holder.targaVeicolo.text= "Targa: ${richiesta.targaveicolo}"


        updateUI(richiesta, holder, stato)




    }

    private fun onButtonClicked(richiesta: Richiesta, nuovoStato:String) {
        val richiestaId = richiesta.richiestaId

        richiestaViewModel.updateRichiestaStato(richiestaId, nuovoStato) { success, errMsg ->
            if (success) {
                Log.d("provastato", "Stato aggiornato con successo: $nuovoStato")
            } else {
                Log.d("provastato", "Errore nell'aggiornamento dello stato: $errMsg")
            }
        }
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

    private fun caricaDettagliVeicolo(holder: MyViewHolder, veicolo: Veicolo){
            holder.nomeVeicolo.text ="Veicolo: ${veicolo.modello}"

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(richiesta:Richiesta, holder: MyViewHolder, stato: String) {
        val context = holder.itemView.context
        val button1=holder.button1
        val button2=holder.button2


        var coloreStato: Int
        when (stato) {
            "Attesa" -> {

                if(userViewModel.checkUserType(userType)){
                    button1.text = "ACCETTA"
                    button2.text = "RIFIUTA"
                }else{
                    button1.visibility = GONE
                    button2.text= "ANNULLA RICHIESTA"
                    button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }


                coloreStato = ContextCompat.getColor(context, R.color.selected_star_color)
                holder.statoRichiesta.setTextColor(coloreStato)

                holder.button1.setOnClickListener {
                    onButtonClicked(richiesta, "Accettata")
                }
                holder.button2.setOnClickListener {
                    onButtonClicked(richiesta, "Rifiutata")
                }
            }
            "Accettata" -> {
                if(userViewModel.checkUserType(userType)){
                    button1.text = "COMPLETATA"
                    button2.text = "ANNULLA"
                }else {
                    button1.visibility = GONE
                    button2.text= "ANNULLA RICHIESTA"
                    button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                coloreStato = ContextCompat.getColor(context, R.color.lime_green)
                holder.statoRichiesta.setTextColor(coloreStato)

                holder.button1.setOnClickListener {
                    if(richiestaViewModel.checkClickOnComplete(richiesta)) {
                        onButtonClicked(richiesta, "Completata")
                    }else{
                        Toast.makeText(context, "La richiesta potrà essere completata a partire dal giorno successivo alla data specificata", Toast.LENGTH_SHORT).show()
                    }
                }

                holder.button2.setOnClickListener {
                    if(richiestaViewModel.checkClickOnAnnulla(richiesta)){
                        onButtonClicked(richiesta, "Rifiutata")
                    }else{
                        Toast.makeText(context, "La richiesta non può essere annulata nel giorno in cui deve essere completata", Toast.LENGTH_SHORT).show()
                    }

                }

            }
            "Completata"->{
                button1.visibility= GONE
                button2.visibility = GONE
                //button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

                coloreStato = ContextCompat.getColor(context, R.color.dark_green)
                holder.statoRichiesta.setTextColor(coloreStato)
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
