package com.example.easymove.model

data class Veicolo(
    val id: String,
    val modello: String,
    val targa: String,
    val capienza: String,
    val citta: String,
    val via: String,
    val codicePostale: String,
    val tariffakm: String,
    val imageUrl: String // URL dell'immagine
){// Aggiungi un costruttore senza argomenti richiesto da Firestore
constructor() : this(
    "", "", "", "", "",
    "", "", "", "")
}

