package com.example.easymove.model
data class User(
    val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val userType: String,
    val imageUrl: String
){
    constructor() : this("", "", "", "", "","")
}

