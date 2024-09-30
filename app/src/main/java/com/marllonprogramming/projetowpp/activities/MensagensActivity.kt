package com.marllonprogramming.projetowpp.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.marllonprogramming.projetowpp.R
import com.marllonprogramming.projetowpp.databinding.ActivityMensagensBinding
import com.marllonprogramming.projetowpp.model.Usuario
import com.marllonprogramming.projetowpp.utils.Constantes

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        recuperarDadosUsuarioDestinatario()

    }

    private fun recuperarDadosUsuarioDestinatario() {
        val extras = intent.extras
        if (extras != null){
            val origem = extras.getString("origem")
            if(origem == Constantes.ORIGEM_CONTATO){
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    extras.getParcelable("dadosDestinatarios", Usuario::class.java)
                }else{
                    extras.getParcelable("dadosDestinatarios")
                }
                /* Mesmo codigo acima escrito de outra forma
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    dadosDestinatario = extras.getParcelable("dadosDestinatarios", Usuario::class.java)
                }else{
                    dadosDestinatario = extras.getParcelable("dadosDestinatarios")
                }*/
            }else if(origem == Constantes.ORIGEM_CONVERSA){

            }
        }
    }
}