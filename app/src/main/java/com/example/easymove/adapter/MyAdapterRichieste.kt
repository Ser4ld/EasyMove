package com.example.easymove.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.View.CreaRecensioneFragment
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.Viewmodel.RecensioneViewModel
import com.example.easymove.model.Richiesta
import com.example.easymove.model.User
import com.example.easymove.model.Veicolo

class MyAdapterRichieste(
    private val richiesteList: ArrayList<Richiesta>,
    private var veicoliList: List<Veicolo> ,
    private var userList: List<User>,
    private var userType: String,
    private val recensioneViewModel: RecensioneViewModel,
    private val richiestaViewModel: RichiestaViewModel,
    private val userViewModel: UserViewModel,
    private val veicoliViewModel: VeicoliViewModel) : RecyclerView.Adapter<MyAdapterRichieste.MyViewHolder>() {

    // Aggiorna la lista delle richieste con nuovi dati
    fun updateRichieste(newDataList: ArrayList<Richiesta>) {
        richiesteList.clear()
        richiesteList.addAll(newDataList)

        // Notifica all'adapter che i dati sono cambiati, richiedendo un aggiornamento della RecyclerView
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        // ViewHolder che contiene i riferimenti agli elementi UI all'interno di ciascuna card di recensione
        val nomeCreatore: TextView= itemView.findViewById(R.id.autoreTextView)
        val imgCreatore: ImageView = itemView.findViewById(R.id.profileImageView)
        val descrizione: TextView=itemView.findViewById(R.id.textDescrizione2)
        val statoRichiesta: TextView=itemView.findViewById(R.id.textStatoRichiesta2)
        val dataRichiesta: TextView= itemView.findViewById(R.id.textData2)
        val puntoPartenza: TextView= itemView.findViewById(R.id.textPuntoPartenza2)
        val puntoArrivo: TextView= itemView.findViewById(R.id.textPuntoArrivo2)
        val nomeVeicolo: TextView= itemView.findViewById(R.id.textNomeVeicolo2)
        val targaVeicolo: TextView= itemView.findViewById(R.id.textTargaVeicolo2)
        val button1: Button = itemView.findViewById(R.id.button1)
        val button2: Button = itemView.findViewById(R.id.button2)
        val prezzo: TextView= itemView.findViewById(R.id.textPrezzo2)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Crea e restituisce una nuova istanza di MyViewHolder quando necessario
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_richiesta, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        // Restituisce il numero totale di elementi nella lista di richieste
        return richiesteList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        // Collega i dati relativi a una specifica posizione
        // nella lista (richiesteList) ai widget UI presenti nel ViewHolder
        var richiesta = richiesteList[position]
        var stato=richiesta.stato


        // Verifica il tipo di utente associato alla richiesta e carica i dettagli in base al tipo di utente.
        if(userViewModel.checkUserType(userType)){
            // Se l'utente è di tipo consumatore, filtra l'utente nella lista degli utenti per l'ID consumatore.
            val user = userViewModel.FilterListById(richiesta.consumatoreId, userList)
            if (user != null) {
                // Se l'utente è presente, carica i dettagli utente nella vista.
                caricaDettagliUtente(holder, user)
            }

        }else {
            // Se l'utente è di tipo guidatore, filtra l'utente nella lista degli utenti per l'ID guidatore.
            val user = userViewModel.FilterListById(richiesta.guidatoreId, userList)
            if (user != null) {
                // Se l'utente è presente, carica i dettagli utente nella vista.
                caricaDettagliUtente(holder, user)
            }

        }


        // Filtra il veicolo associato alla richiesta per targa e carica i dettagli del veicolo.
        val veicolo = veicoliViewModel.filterListbyTarga(richiesta.targaveicolo, veicoliList)
        if(veicolo != null){
            caricaDettagliVeicolo(holder,veicolo)
        }

        // Imposta i testi degli oggetti UI secondo le caratteristiche della richiesta
        holder.descrizione.text = richiesta.descrizione
        holder.statoRichiesta.text = stato
        holder.dataRichiesta.text = richiesta.data
        holder.puntoPartenza.text = richiesta.puntoPartenza
        holder.puntoArrivo.text = richiesta.puntoArrivo
        holder.targaVeicolo.text= richiesta.targaveicolo
        holder.prezzo.text = richiesta.prezzo + " €"


        // Aggiorna l'aspetto della vista in base allo stato attuale della richiesta.
        updateUI(richiesta, holder, stato)

    }

    // Funzione chiamata quando viene cliccato un pulsante associato a un
    // cambiamento dello stato di una richiesta
    private fun onButtonClicked(richiesta: Richiesta, nuovoStato:String) {
        val richiestaId = richiesta.richiestaId

        // Aggiorna lo stato della richiesta
        richiestaViewModel.updateRichiestaStato(richiestaId, nuovoStato) { success, errMsg ->
            if (success) {
                Log.d("provastato", "Stato aggiornato con successo: $nuovoStato")
            } else {
                Log.d("provastato", "Errore nell'aggiornamento dello stato: $errMsg")
            }
        }
    }


    // Carica i dettagli dell'utente come il nome, cognome e l'immagine profilo nella vista
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

    // Carica il nome del veicolo nella vista
    private fun caricaDettagliVeicolo(holder: MyViewHolder, veicolo: Veicolo){
            holder.nomeVeicolo.text =veicolo.modello

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(richiesta:Richiesta, holder: MyViewHolder, stato: String) {
        val context = holder.itemView.context
        val button1=holder.button1
        val button2=holder.button2

        // Inizializzazione del colore dello stato
        var coloreStato: Int

        // Gestione degli stati della richiesta
        when (stato) {

            // Gestione richiesta in attesa
            "Attesa" -> {
                // Configurazione dei pulsanti in base al tipo di utente
                if(userViewModel.checkUserType(userType)){
                    // guidatore
                    button1.text = "ACCETTA"
                    button2.text = "RIFIUTA"
                }else{
                    // consumatore
                    button1.visibility = GONE
                    button2.text= "ANNULLA RICHIESTA"
                    button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }


                // Impostazione del colore dello stato
                coloreStato = ContextCompat.getColor(context, R.color.selected_star_color)
                holder.statoRichiesta.setTextColor(coloreStato)


                // Gestione dei clic sui pulsanti che permettono di aprire un dialog
                // di conferma che può modificare lo stato della richiesta
                holder.button1.setOnClickListener {
                    dialog(holder, richiesta, "Accettata")
                }
                holder.button2.setOnClickListener {
                    dialog(holder, richiesta, "Rifiutata")
                }
            }

            // Gestione richiesta in attesa
            "Accettata" -> {
                // Configurazione dei pulsanti in base al tipo di utente
                if(userViewModel.checkUserType(userType)){
                    // guidatore
                    button1.text = "COMPLETATA"
                    button2.text = "ANNULLA"
                }else {
                    // consumatore
                    button1.visibility = GONE
                    button2.text= "ANNULLA RICHIESTA"
                    button2.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                // Impostazione del colore dello stato
                coloreStato = ContextCompat.getColor(context, R.color.lime_green)
                holder.statoRichiesta.setTextColor(coloreStato)

                // Gestione dei clic sui pulsanti
                holder.button1.setOnClickListener {

                    // Verifica se è possibile completare la richiesta
                    if(richiestaViewModel.checkClickOnComplete(richiesta)) {
                        dialog(holder, richiesta, "Completata")
                    }else{
                        Toast.makeText(context, "La richiesta potrà essere completata a partire dal giorno successivo alla data specificata", Toast.LENGTH_SHORT).show()
                    }
                }

                holder.button2.setOnClickListener {
                    // Controlla se la richiesta può essere rifiutata
                    if(richiestaViewModel.checkClickOnAnnulla(richiesta)){
                        dialog(holder, richiesta, "Rifiutata")
                    }else{
                        Toast.makeText(context, "La richiesta non può essere annulata nel giorno in cui deve essere completata", Toast.LENGTH_SHORT).show()
                    }

                }

            }
            // Gestione richiesta completata
            "Completata"->{

                // Configurazione dei pulsanti in base al tipo di utente
                if(userViewModel.checkUserType(userType)){
                    // Se è guidatore non può fare nulla con la richiesta completata
                    button1.visibility= GONE

                } else{
                    // Se è consumatore può lasciare una recensione al servizio
                    button1.visibility= VISIBLE
                    button1.text="Fai una recensione"
                    button1.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

                    // Verifica se il guidatore ha già fatto una recensione per questa richiesta
                    recensioneViewModel.chcekRecensione(richiesta.richiestaId){success ->
                        if(success){
                            // in caso abbia fatto la recensione il bottone non deve essere visibile
                            button1.visibility  = GONE
                        }
                    }

                    // Gestione del clic sul pulsante per fare una recensione
                    button1.setOnClickListener {

                        // Al fragment di recensioen verrà passato l'id del
                        // guidatore e l'id della richiesta
                        val bundle = Bundle()
                        bundle.putString("guidatoreId", richiesta.guidatoreId)
                        bundle.putString("richiestaId", richiesta.richiestaId)

                        val creaRecensioneFragment = CreaRecensioneFragment()
                        creaRecensioneFragment.arguments = bundle

                        // Viene sostituito il fragment corrente con il creaRecensioneFragment
                        val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, creaRecensioneFragment)
                            .addToBackStack(null)
                            .commit()
                    }

                }

                // Nascondi il secondo pulsante
                button2.visibility = GONE


                // Impostazione del colore dello stato
                coloreStato = ContextCompat.getColor(context, R.color.dark_green)
                holder.statoRichiesta.setTextColor(coloreStato)


            }
            // Gestione richiesta annullata
            else -> {

                // Nascondi entrambi i pulsanti
                button1.visibility= GONE
                button2.visibility = GONE

                // Impostazione del colore dello stato
                coloreStato = ContextCompat.getColor(context, R.color.red)
                holder.statoRichiesta.setTextColor(coloreStato)

            }
        }
    }

    // Mostra un dialog personalizzato per confermare un'azione sulla Richiesta
    private fun dialog(holder: MyViewHolder, richiesta: Richiesta, stato: String) {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(holder.itemView.context)
        val customView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        // Imposta lo sfondo del dialog come trasparente per applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Imposta il testo e l'icona del messaggio nel layout personalizzato
        customView.findViewById<TextView>(R.id.textView2).text = "Sei sicuro di eseguire l'operazione ?"
        customView.findViewById<ImageView>(R.id.imageView2).setImageDrawable(holder.itemView.context.getDrawable(R.drawable.baseline_announcement_24))


        // Imposta l'azione del pulsante "No" per chiudere il dialog
        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }

        // Imposta l'azione del pulsante "Sì" per eseguire l'azione di modifica stato della richiesta
        // chiudere il dialog
        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{
            onButtonClicked(richiesta, stato)
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()

    }
}
