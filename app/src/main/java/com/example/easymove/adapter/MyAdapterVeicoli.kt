package com.example.easymove.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easymove.R
import com.example.easymove.View.*
import com.example.easymove.ViewModel.RichiestaViewModel
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.model.Richiesta
import com.example.easymove.model.User
import com.example.easymove.model.Veicolo
import java.util.regex.Pattern


class MyAdapterVeicoli(private val veicoliViewModel: VeicoliViewModel,
                       private val userViewModel: UserViewModel,
                       private val richiestaViewModel: RichiestaViewModel,
                       private val list:ArrayList<Veicolo>,
                       private val richiesteList: List<Richiesta>,
                       private val distance: String,
                       private val userList: List<User>?):RecyclerView.Adapter<MyAdapterVeicoli.MyViewHolder>() {

    init{
        list.sortedBy { it.modello }
    }

    // Aggiorna i dati nell'adapter con una nuova lista di veicoli
    fun updateData(newDataList: ArrayList<Veicolo>) {
        // Cancella i dati esistenti
        list.clear()

        // Ordina la nuova lista in base al modello del veicolo
        newDataList.sortBy { it.modello }

        // Aggiunge i nuovi dati alla lista dell'adapter
        list.addAll(newDataList)

        // Notifica alla RecyclerView che i dati sono cambiati
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        // ViewHolder che contiene i riferimenti agli elementi UI all'interno di ciascuna card veicolo
        val modello: TextView= itemView.findViewById(R.id.modello)
        val targa: TextView= itemView.findViewById(R.id.targa)
        val capienza: TextView= itemView.findViewById(R.id.capienza)
        val locazione: TextView = itemView.findViewById(R.id.locazione)
        val annuncioImageView: ImageView = itemView.findViewById(R.id.annuncioImageView)
        val prezzo: TextView = itemView.findViewById(R.id.prezzo)
        val guidatoreText: TextView = itemView.findViewById(R.id.annuncioGuidatoreText)
        val imageGuidatore: ImageView = itemView.findViewById(R.id.annuncioGuidatoreImage)

        val button: Button = itemView.findViewById(R.id.btnRichiediTrasporto)
        val btnElimina: Button = itemView.findViewById(R.id.btnEliminaVeicolo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // Crea e restituisce una nuova istanza di MyViewHolder quando necessario
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_veicolo, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        // Restituisce il numero totale di elementi nella lista di veicoli
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        // Collega i dati relativi a una specifica posizione
        // nella lista dei veicoli ai widget UI presenti nel ViewHolder
        var veicolo= list[position]

        // Ottieni il fragment corrente nell'activity, in modo da adattare il comportamento
        // in base al fragment in cui è visualizzata la lista dei veicoli.
        val currentFragment = (holder.itemView.context as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.fragmentContainer)


        // Verifica se il fragment corrente è ListaVeicoliFragment, che rappresenta la lista di veicoli
        if (currentFragment is ListaVeicoliFragment) {

            // Calcola e imposta il prezzo basato sulla distanza e la tariffa al chilometro del
            // veicolo corrente tramite il metodo calcolaPrezzo
            holder.prezzo.text = richiestaViewModel.calcolaPrezzo(distance, list[position].tariffakm).toString()

            // Ottieni le informazioni sul guidatore associato al veicolo corrente
            val user = userViewModel.FilterListById(list[position].id, userList!!)
            if (user != null){
                setGuidatoreInformation(holder, user)
            }

            // Imposta la visibilità e il comportamento dei pulsanti per interagire con la lista dei veicoli
            holder.btnElimina.visibility = GONE
            holder.button.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

            // Imposta un listener per il clic sul pulsante associato a un veicolo nella RecyclerView
            holder.button.setOnClickListener {
                // Richiama il metodo onVeicoloClicked del veicoliViewModel con la posizione del veicolo nella lista
                veicoliViewModel.onVeicoloClicked(position)
            }

            // Gestisci il click sulla visualizzazione del profilo del guidatore associato al veicolo
            holder.guidatoreText.setOnClickListener{

                // Passa al fragment ProfileGuidatoreUserModeFragment l'ID del guidatore come parametro
                val bundle = Bundle()
                bundle.putString("idguidatore", veicolo.id)
                val profiloGuidatoreUserModeFragment = ProfileGuidatoreUserModeFragment()
                profiloGuidatoreUserModeFragment.arguments = bundle

                // rimpiazza il fragment corrente con il profiloGuidatoreUserModeFragment
                replaceFragment(holder, profiloGuidatoreUserModeFragment)
            }

        }
        else {
            // Se il fragment corrente non è ListaVeicoliFragment, adatta il comportamento
            // per la visualizzazione in altri fragment come ModificaVeicoloFragment.

            // Imposta il prezzo del veicolo come tariffa al chilometro
            holder.prezzo.text = veicolo.tariffakm+ " €/Km"

            // Nascondi le informazioni sul guidatore
            holder.guidatoreText.visibility = GONE
            holder.imageGuidatore.visibility = GONE

            // Adatta la visualizzazione e il comportamento dei pulsanti per la modifica e l'eliminazione del veicolo
            holder.button.text= "Modifica Veicolo"
            holder.button.setOnClickListener {

                // Passa al fragment ModificaVeicoloFragment la targa del veicolo come parametro
                val bundle = Bundle()
                bundle.putString("targa", veicolo.targa)
                val modificaVeicoloFragment = ModificaVeicoloFragment()
                modificaVeicoloFragment.arguments = bundle
                replaceFragment(holder, modificaVeicoloFragment)
            }

            // Gestisci il click sul pulsante per l'eliminazione del veicolo che fa apparire un dialog
            holder.btnElimina.setOnClickListener {
                dialog(holder,veicolo, richiesteList)
            }
        }

        // Nascondi il pulsante nel fragment VeicoliGuidatorePublicFragment
        if (currentFragment is VeicoliGuidatorePublicFragment){
            holder.button.visibility = GONE
            holder.btnElimina.visibility = GONE
        }

        // Imposta le informazioni comuni del veicolo nella vista
        holder.modello.text = veicolo.modello
        holder.targa.text = veicolo.targa
        holder.capienza.text = veicolo.capienza
        holder.locazione.text = veicolo.via


        // Carica l'immagine del veicolo dalla URL utilizzando la libreria Glide, se disponibile
        if (!list[position].imageUrl.isNullOrEmpty()) {

            Glide.with(holder.itemView.context)
                .load(veicolo.imageUrl)
                .into(holder.annuncioImageView)
        } else {
            // Se l'URL dell'immagine non è disponibile, carica un'immagine di default
            holder.annuncioImageView.setImageResource(R.drawable.baseline_image_24)
        }
    }

    // Imposta le informazioni del guidatore nella vista, inclusa l'immagine del profilo se disponibile
    private fun setGuidatoreInformation(holder: MyViewHolder,user: User){
        holder.guidatoreText.text = "${user.name} ${user.surname}"

        if (!user.imageUrl.isNullOrEmpty()) {

            Glide.with(holder.itemView.context)
                .load(user.imageUrl)
                .circleCrop()
                .into(holder.imageGuidatore)
        } else {
            holder.imageGuidatore.setImageResource(R.drawable.baseline_image_24)
        }
    }

    // Sostituisce il fragment corrente con un nuovo fragment passato come parametro
    private fun replaceFragment(holder:MyViewHolder,fragment: Fragment){
        (holder.itemView.context as AppCompatActivity)
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    // Visualizza un dialog personalizzato per confermare un'operazione
    private fun dialog(holder: MyViewHolder, veicolo: Veicolo, richieste: List<Richiesta>) {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(holder.itemView.context)
        val customView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        // Imposta uno sfondo trasparente per applicare un background con i bordi arrotondati al dialog
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customView.findViewById<TextView>(R.id.textView2).text = "Sei sicuro di eseguire l'operazione ?"
        customView.findViewById<ImageView>(R.id.imageView2).setImageDrawable(holder.itemView.context.getDrawable(R.drawable.baseline_announcement_24))

        // Imposta il listener per il pulsante "No" nel dialog per chiudere il dialog senza eseguire l'operazione
        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }

        // Imposta il listener per il pulsante "Yes" nel dialog per confermare l'operazione
        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{

            // controlla che ci siano delle richieste nello stato di accettate riguardanti il veicolo,
            // in quel caso non si può eliminare
            richiestaViewModel.checkRichiestaOnDeleteVeicolo(veicolo.id, richiesteList){success, message->
                if(success){

                    Toast.makeText(holder.itemView.context, "Impossibile eliminare il veicolo: sono presenti delle richieste Accettate", Toast.LENGTH_SHORT).show()

                }else{

                    // Altrimenti, aggiorna le richieste tramite updateRichiestaonDeleteVeicolo
                    // e procedi con l'eliminazione del veicolo
                    richiestaViewModel.updateRichiestaonDeleteVeicolo(veicolo.id, richiesteList)
                    veicoliViewModel.deleteVeicolo(veicolo.targa){success, message ->
                        if(success){
                            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
                            Log.d("aggiorna", list.toString())
                        }else {
                            Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }

            // chiude il dialog
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()

    }
}