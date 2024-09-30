package com.marllonprogramming.projetowpp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val foto: String = ""
): Parcelable
