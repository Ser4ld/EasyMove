package com.example.easymove.model

data class Richiesta(
    val annuncioId: String? = null,
    val mittenteId: String? = null,
    val stato: StatoRichiesta = StatoRichiesta.IN_ATTESA
)

enum class StatoRichiesta { IN_ATTESA, ACCETTATA, RIFIUTATA }