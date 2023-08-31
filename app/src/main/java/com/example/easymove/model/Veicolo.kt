package com.example.easymove.model

data class Veicolo(
    val id: String,
    var modello: String,
    val targa: String,
    val capienza: String,
    var citta: String,
    var via: String,
    var codicePostale: String,
    var tariffakm: String,
    var imageUrl: String // URL dell'immagine
){// Aggiungi un costruttore senza argomenti richiesto da Firestore
constructor() : this(
    "", "", "", "", "",
    "", "", "", "")
}

