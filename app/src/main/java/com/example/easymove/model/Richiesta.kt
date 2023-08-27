package com.example.easymove.model

data class Richiesta(
    var annuncioId: String,
    val guidatoreId: String,
    val consumatoreId: String,
    val targaveicolo: String,
    val puntoPartenza: String,
    val puntoArrivo: String,
    val data: String,
    val descrizione: String,
    val stato: String

)
