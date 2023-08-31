package com.example.easymove.adapter

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
import com.example.easymove.ViewModel.UserViewModel
import com.example.easymove.ViewModel.VeicoliViewModel
import com.example.easymove.model.User
import com.example.easymove.model.Veicolo
import java.util.regex.Pattern


class MyAdapterVeicoli(private val veicoliViewModel: VeicoliViewModel,private val userViewModel: UserViewModel,private val list:ArrayList<Veicolo>, private val distance: String, private val userList: List<User>?):RecyclerView.Adapter<MyAdapterVeicoli.MyViewHolder>() {

    init {
        list.sortBy { it.modello }
    }

    fun updateData(newDataList: ArrayList<Veicolo>) {
        list.clear()
        newDataList.sortBy { it.tariffakm }
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
            holder.prezzo.text = (list[position].tariffakm.toDouble()*extractNumbersFromString(distance)).toString()+" €"
            Log.d("provaaaa", userList.toString())
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
                veicoliViewModel.deleteVeicolo(veicolo.targa){ success, message ->
                    Toast.makeText(holder.itemView.context, message, Toast.LENGTH_SHORT).show()
                }
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

    private fun extractNumbersFromString(input: String): Double {
        val pattern = Pattern.compile("\\d+(\\.\\d+)?") // Crea un pattern per trovare sequenze di numeri
        val matcher = pattern.matcher(input)

        if (matcher.find()) {
            return matcher.group().toDouble()  // Converte la sequenza di numeri in un intero
        }

        return 0.0 // Ritorna un valore di default se non trova alcun numero
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
}