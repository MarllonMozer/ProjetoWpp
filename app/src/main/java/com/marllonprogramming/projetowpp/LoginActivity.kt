package com.marllonprogramming.projetowpp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.marllonprogramming.projetowpp.databinding.ActivityLoginBinding
import com.marllonprogramming.projetowpp.utils.exibirMensagem

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var email: String
    private lateinit var senha: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
        //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            startActivity(
                Intent(this,MainActivity::class.java)
            )
        }
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this,CadastroActivity::class.java)
            )
        }
        binding.btnLogar.setOnClickListener { 
            if(validarCampos()){
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(email,senha)
            .addOnSuccessListener{
                exibirMensagem("Logado com sucesso")
                startActivity(
                    Intent(this,MainActivity::class.java)
                )
            }
            .addOnFailureListener { erro ->
                try {
                    throw erro
                }catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException) {
                    erroUsuarioInvalido.printStackTrace()
                    exibirMensagem("E-mail nao Cadastrado")
                }catch (erroCredencialInvalida: FirebaseAuthInvalidCredentialsException) {
                    erroCredencialInvalida.printStackTrace()
                    exibirMensagem("E-mail ou Senha inválidos")
                }
            }

    }

    private fun validarCampos(): Boolean {

        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

            if (email.isNotEmpty()) {
                binding.textInputLayoutLoginEmail.error = null
                if (senha.isNotEmpty()) {
                    binding.textInputLayoutLoginSenha.error = null
                    return true
                } else {
                    binding.textInputLayoutLoginSenha.error = "Preencha a sua senha!"
                    return false
                }
            } else {
                binding.textInputLayoutLoginEmail.error = "Preencha o seu e-mail!"
                return false
            }
    }
}