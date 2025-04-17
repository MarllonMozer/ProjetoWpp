package com.marllonprogramming.projetowpp.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.marllonprogramming.projetowpp.adapter.MensagensAdapter
import com.marllonprogramming.projetowpp.databinding.ActivityMensagensBinding
import com.marllonprogramming.projetowpp.model.Conversa
import com.marllonprogramming.projetowpp.model.Mensagem
import com.marllonprogramming.projetowpp.model.Usuario
import com.marllonprogramming.projetowpp.utils.Constantes
import com.marllonprogramming.projetowpp.utils.exibirMensagem
import com.squareup.picasso.Picasso

class MensagensActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var  conversasAdapter: MensagensAdapter
    private lateinit var listenerRegistration: ListenerRegistration
    private var dadosDestinatario: Usuario? = null
    private var dadosUsuarioLogado: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUsuario()
        inicializarToolbar()
        inicializerEventosDeClick()
        inicializarRecyclerView()
        inicializarListeners()
    }

    private fun inicializarRecyclerView() {
        with(binding){
            conversasAdapter = MensagensAdapter()
            rvMensagem.adapter = conversasAdapter
            rvMensagem.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListeners() {

        val idUsuarioRemetente = firebaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if(idUsuarioRemetente != null && idUsuarioDestinatario != null) {

            listenerRegistration = firestore
                .collection(Constantes.BD_MENSAGENS)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data",Query.Direction.ASCENDING)
                .addSnapshotListener{querySnapshot, erro ->
                    if(erro != null){
                        exibirMensagem("Erro ao carregar mensagens")
                    }
                    val listaMensagem = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents
                    documentos?.forEach { documentSnapshot ->
                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                        if(mensagem != null){
                            listaMensagem.add(mensagem)
                            Log.i("exibiçao_mensagem",mensagem.mensagem)
                        }
                    }
                    //Lista
                    if(listaMensagem.isNotEmpty()){
                        //Carregar os dados no Adapter
                        conversasAdapter.adicionarLista(listaMensagem)
                    }
                }
        }
    }

    private fun inicializerEventosDeClick() {
        binding.fabEnviar.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {

        if(textoMensagem.isEmpty()){
            val idUsuarioRemetente = firebaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if(idUsuarioRemetente != null && idUsuarioDestinatario != null){
                val mensagem = Mensagem(
                    idUsuarioRemetente,textoMensagem
                )
                //Salvar para o remetente
               salvarMensagemFirestore(
                   idUsuarioRemetente, idUsuarioDestinatario, mensagem
               )

               val conversaRemetente = Conversa(
                   idUsuarioRemetente,
                   idUsuarioDestinatario,
                   dadosDestinatario!!.nome,
                   dadosDestinatario!!.foto,
                   textoMensagem
               )

                salvarConversaFirestore(conversaRemetente)

                //Salvar mesma mensagem para o destinatario
                salvarMensagemFirestore(
                    idUsuarioDestinatario, idUsuarioRemetente, mensagem
               )

                val conversaDestinatario = Conversa(
                    idUsuarioDestinatario,
                    idUsuarioRemetente,
                    dadosUsuarioLogado!!.nome,dadosUsuarioLogado!!.foto,
                    textoMensagem
                )

                salvarConversaFirestore(conversaDestinatario)

                binding.editMensagem.setText("")
            }
        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore.collection(Constantes.CONVERSAS)
            .document(conversa.idUsuarioRemetente)
            .collection(Constantes.ULTIMAS_CONVERSAS)
            .document(conversa.idUsuarioDestinatario)
            .set(conversa)
            .addOnFailureListener {
             exibirMensagem("Erro ao salvar conversa")
            }

    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String,
        idUsuarioDestinatario: String,
        mensagem: Mensagem
    ) {
        firestore
            .collection(Constantes.BD_MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao enviar mensagem")
            }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagem
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if( dadosDestinatario != null){
                binding.textNomePerfil.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imageFotoPerfil)
            }else{
                binding.textNomePerfil.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imageFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUsuario() {
        //Dados do usuario logado
        val idUsuarioLogado = firebaseAuth.currentUser?.uid
        if(idUsuarioLogado != null) {
            firestore.collection(Constantes.USUARIOS)
                .document(idUsuarioLogado)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if(usuario != null){
                        dadosUsuarioLogado = usuario
                    }
                }
        }
        //Recuperando dados destinatario
        val extras = intent.extras
        if (extras != null){
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                extras.getParcelable("dadosDestinatario", Usuario::class.java)
            }else{
                extras.getParcelable("dadosDestinatario")
            }
            /* Mesmo codigo acima escrito de outra forma
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                dadosDestinatario = extras.getParcelable("dadosDestinatarios", Usuario::class.java)
            }else{
                dadosDestinatario = extras.getParcelable("dadosDestinatarios")
            }*/
            }
        }
    }
}