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
        val prezzo: TextView = itemView.findViewById(R.id.prezzo)
        val guidatoreText: TextView = itemView.findViewById(R.id.annuncioGuidatoreText)
        val imageGuidatore: ImageView = itemView.findViewById(R.id.annuncioGuidatoreImage)

        val button: Button = itemView.findViewById(R.id.btnRichiediTrasporto)
        val btnElimina: Button = itemView.findViewById(R.id.btnEliminaVeicolo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_veicolo, parent, false)
    return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
    return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var veicolo= list[position]

        val currentFragment = (holder.itemView.context as AppCompatActivity)
            .supportFragmentManager
            .findFragmentById(R.id.fragmentContainer)

        if (currentFragment is ListaVeicoliFragment) {
            holder.prezzo.text = richiestaViewModel.calcolaPrezzo(distance, list[position].tariffakm).toString()
            val user = userViewModel.FilterListById(list[position].id, userList!!)
            if (user != null){
                setGuidatoreInformation(holder, user)
            }

            holder.btnElimina.visibility = GONE
            holder.button.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT

            holder.button.setOnClickListener {
                veicoliViewModel.onVeicoloClicked(position)
            }

            holder.guidatoreText.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("idguidatore", veicolo.id)
                val profiloGuidatoreUserModeFragment = ProfileGuidatoreUserModeFragment()
                profiloGuidatoreUserModeFragment.arguments = bundle
                replaceFragment(holder, profiloGuidatoreUserModeFragment)
            }

        }
        else {
            holder.prezzo.text = veicolo.tariffakm+ " €/Km"
            holder.guidatoreText.visibility = GONE
            holder.imageGuidatore.visibility = GONE
            holder.button.text= "Modifica Veicolo"

            holder.button.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("targa", veicolo.targa)
                val modificaVeicoloFragment = ModificaVeicoloFragment()
                modificaVeicoloFragment.arguments = bundle
                replaceFragment(holder, modificaVeicoloFragment)
            }

            holder.btnElimina.setOnClickListener {
                dialog(holder,veicolo, richiesteList)
            }
        }

        if (currentFragment is VeicoliGuidatorePublicFragment){
            holder.button.visibility = GONE
            holder.btnElimina.visibility = GONE
        }

        holder.modello.text = veicolo.modello
        holder.targa.text = veicolo.targa
        holder.capienza.text = veicolo.capienza
        holder.locazione.text = veicolo.via


        if (!list[position].imageUrl.isNullOrEmpty()) {

            // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
            Glide.with(holder.itemView.context)
                .load(veicolo.imageUrl)
                .into(holder.annuncioImageView)
        } else {
            // Carica l'immagine di default
            holder.annuncioImageView.setImageResource(R.drawable.baseline_image_24)
        }
    }

    private fun setGuidatoreInformation(holder: MyViewHolder,user: User){
        holder.guidatoreText.text = "${user.name} ${user.surname}"

        if (!user.imageUrl.isNullOrEmpty()) {

            // Carica l'immagine relativa all'Url (firebase storage) utilizzando la libreria Glide
            Glide.with(holder.itemView.context)
                .load(user.imageUrl)
                .circleCrop()
                .into(holder.imageGuidatore)
        } else {
            // Carica l'immagine di default
            holder.imageGuidatore.setImageResource(R.drawable.baseline_image_24)
        }
    }

    private fun replaceFragment(holder:MyViewHolder,fragment: Fragment){
        (holder.itemView.context as AppCompatActivity)
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun dialog(holder: MyViewHolder, veicolo: Veicolo, richieste: List<Richiesta>) {

        // Crea un nuovo AlertDialog
        val builder = AlertDialog.Builder(holder.itemView.context)
        val customView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.custom_layout_dialog, null)
        builder.setView(customView)
        val dialog = builder.create()

        //imposto lo sfodo del dialog a trasparente per poter applicare un background con i bordi arrotondati
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customView.findViewById<TextView>(R.id.textView2).text = "Sei sicuro di eseguire l'operazione ?"
        customView.findViewById<ImageView>(R.id.imageView2).setImageDrawable(holder.itemView.context.getDrawable(R.drawable.baseline_announcement_24))

        customView.findViewById<Button>(R.id.btn_no).setOnClickListener{
            dialog.dismiss()
        }

        customView.findViewById<Button>(R.id.btn_yes).setOnClickListener{
            richiestaViewModel.checkRichiestaOnDeleteVeicolo(veicolo.id, richiesteList){success, message->
                if(success){
                    Toast.makeText(holder.itemView.context, "Impossibile eliminare il veicolo: sono presenti delle richieste Accettate", Toast.LENGTH_SHORT).show()
                }else{
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
            dialog.dismiss()
        }

        // Mostra il popup
        dialog.show()

    }
}