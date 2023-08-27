package com.example.easymove.Viewmodel

import androidx.lifecycle.ViewModel
import com.example.easymove.repository.RecensioneRepository

class RecensioneViewModel: ViewModel() {

    val recensioneRepository = RecensioneRepository()

    fun creaRecensione( stelline: String, descrizione: String, callback: (success: Boolean, errorMessage: String?) -> Unit
    ){
        if(stelline!="0.0" && descrizione.isNotEmpty()){
            recensioneRepository.storeRecensione(stelline,descrizione){success, message->
                if(success){
                    callback(true, "Recensione Inviata")
                }else{
                    callback(false, message)
                }
            }
        } else{
        callback(false, "Compilare tutti i campi")
        }
    }
}