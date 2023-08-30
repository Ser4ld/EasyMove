package com.example.easymove.model

data class Recensione(
    var richiestaId: String,
    var recensioneId: String,
    val guidatoreId: String,
    val consumatoreId: String,
    val valutazione: String,
    val descrizione: String
){
constructor() : this("","","", "", "", "")
}
