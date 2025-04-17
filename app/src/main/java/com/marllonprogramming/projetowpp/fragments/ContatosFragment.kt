package com.marllonprogramming.projetowpp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.marllonprogramming.projetowpp.activities.MensagensActivity
import com.marllonprogramming.projetowpp.adapter.ContatosAdapter
import com.marllonprogramming.projetowpp.databinding.FragmentContatosBinding
import com.marllonprogramming.projetowpp.model.Usuario
import com.marllonprogramming.projetowpp.utils.Constantes

class ContatosFragment : Fragment() {

    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate(
            inflater,
            container,
            false
        )
        contatosAdapter = ContatosAdapter{ usuario ->
            val intent = Intent(context, MensagensActivity::class.java)
            intent.putExtra("dadosDestinatario",usuario)
            //intent.putExtra("origem",Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
       /*
       Quando utiliza o Listerner quando adiciona um novo usuario ele recupera os dados novamente .
        Mas pode acontecer do usuario nao esta na tela entao eu nao quero que o Listerner funcione,
        por isso eu vou remover ele no OnDestroy .
        */
        eventoSnapshot = firestore
            .collection(Constantes.USUARIOS)
            .addSnapshotListener { querySnapshot, error ->
                val listaContatos = mutableListOf<Usuario>()
                val documentos = querySnapshot?.documents
                documentos?.forEach { documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    val idUsuarioLogado = firebaseAuth.currentUser?.uid

                    if (usuario != null && idUsuarioLogado != null) {
                        //Log.i("fragmento_contatos", "Usuario: ${usuario.nome}")
                        if (idUsuarioLogado != usuario.id) {
                            listaContatos.add(usuario)
                        }
                    }
                }
                //Lista de contatos para atualizar o RecyclerView
                if (listaContatos.isNotEmpty()){
                    contatosAdapter.adicionarLista(listaContatos)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }
}

