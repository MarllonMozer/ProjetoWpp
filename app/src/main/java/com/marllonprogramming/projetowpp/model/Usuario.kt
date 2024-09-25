package com.marllonprogramming.projetowpp.model

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val foto: String = ""
)
