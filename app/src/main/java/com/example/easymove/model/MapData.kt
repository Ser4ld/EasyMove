package com.example.easymove.model


data class MapData (
    var address: String,
    var city: String,
    var province: String,
    var region: String,
    var country: String,
    var postalCode: String,
    var latitude:String,
    var longitude: String
){
    // Costruttore personalizzato che crea un oggetto MapData vuoto
    constructor() : this("", "", "", "", "", "", "", "")
}
