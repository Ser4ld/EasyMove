package com.example.easymove.model

import com.example.easymove.enum.StatoRichiesta

data class Richiesta(
    val annuncioId: String,
    val guidatoreId: String,
    val consumatoreId: String,
    val targaveicolo: String,
    val puntoPartenza: String,
    val puntoArrivo: String,
    val data: String,
    val descrizione: String,
    val stato: StatoRichiesta = StatoRichiesta.IN_ATTESA
)
