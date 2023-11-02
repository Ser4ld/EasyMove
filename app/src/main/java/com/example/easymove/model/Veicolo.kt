package com.example.easymove.model

import com.google.android.gms.maps.model.LatLng

data class Veicolo(
    val id: String,
    var modello: String,
    val targa: String,
    val capienza: String,
    var citta: String,
    var via: String,
    var codicePostale: String,
    var latitude:String,
    var longitude:String,
    var tariffakm: String,
    var imageUrl: String // URL dell'immagine
){
constructor() : this(
    "", "", "", "", "",
    "", "", "", "","","")
}

