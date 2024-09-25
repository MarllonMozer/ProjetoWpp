package com.marllonprogramming.projetowpp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.marllonprogramming.projetowpp.databinding.ActivityCadastroBinding
import com.marllonprogramming.projetowpp.model.Usuario
import com.marllonprogramming.projetowpp.utils.exibirMensagem

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        inicializarToolbar()
        inicializarEventosClique()

    }

    private fun inicializarEventosClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()) {
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
            firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { resultado ->
                    if (resultado.isSuccessful) {

                        //Salvar dados do usuário no Firestore
                        /*
                        ID, Nome, Email, Foto
                         */
                        val idUsuario = resultado.result.user?.uid
                        if (idUsuario != null) {
                            val usuario = Usuario(
                                idUsuario,
                                nome,
                                email,
                            )
                            salvarUsuarioFirestore(usuario)
                        }
                        exibirMensagem("Cadastro realizado com sucesso!")
                    }
                }.addOnFailureListener { erro ->
                    try {
                        //throw quer dizer "lançar"
                        throw erro
                    }catch (erroSenhafraca: FirebaseAuthWeakPasswordException) {
                        erroSenhafraca.printStackTrace()
                        exibirMensagem("Senha fraca, digite outra mais forte!")
                    }catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                        erroUsuarioExistente.printStackTrace()
                        exibirMensagem("E-mail ja cadastrado")
                    }catch (erroCredencialInvalida: FirebaseAuthInvalidCredentialsException) {
                        erroCredencialInvalida.printStackTrace()
                        exibirMensagem("E-mail inválido, digite um e-mail válido!")
                    }
                }
    }
    /*OnCompleteListener = vou precisar de um retorno/pegar algum dado.
     onSucessListener = saber se deu certo
     */

    private fun salvarUsuarioFirestore(usuario: Usuario) {

        firestore
            .collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Usuário cadastrado com sucesso!")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.addOnFailureListener{
                exibirMensagem("Erro ao cadastrar usuário!")
            }


    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if (nome.isNotEmpty()) {
            binding.textInputLayoutNome.error = null
            if (email.isNotEmpty()) {
                binding.textInputLayoutEmail.error = null
                if (senha.isNotEmpty()) {
                    binding.textInputLayoutSenha.error = null
                    return true
                } else {
                    binding.textInputLayoutSenha.error = "Preencha a sua senha!"
                    return false
                }
            } else {
                binding.textInputLayoutEmail.error = "Preencha o seu e-mail!"
                return false
            }
        } else {
            binding.textInputLayoutNome.error = "Preencha o seu nome!"
            return false
        }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeToolbarCadastro.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}