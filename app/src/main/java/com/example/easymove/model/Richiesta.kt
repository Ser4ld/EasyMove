package com.example.easymove.model

import com.example.easymove.enum.StatoRichiesta

data class Richiesta(
    val annuncioId: String? = null,
    val mittenteId: String? = null,
    val stato: StatoRichiesta = StatoRichiesta.IN_ATTESA
)
