package com.example.easymove.model

data class Recensione(
    var idRecensione: String,
    val idCreatore: String,
    val idRicevitore: String,
    val stelline: String,
    val descrizione: String
){
constructor() : this("","", "", "", "")
}
