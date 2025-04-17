package com.marllonprogramming.projetowpp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.marllonprogramming.projetowpp.databinding.ItemMensagensDestinatariosBinding
import com.marllonprogramming.projetowpp.databinding.ItemMensagensRemetentesBinding
import com.marllonprogramming.projetowpp.model.Mensagem
import com.marllonprogramming.projetowpp.utils.Constantes

class MensagensAdapter : Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(lista: List<Mensagem>) {
        listaMensagens = lista
        notifyDataSetChanged()
    }

    class MensagemRemetenteViewHolder(
        private val binding: ItemMensagensRemetentesBinding
    ) : ViewHolder(binding.root) {
        fun bind(mensagem: Mensagem){
            binding.textMensagemRemetente.text = mensagem.mensagem
        }
        companion object {
            fun inflarLayout(parent: ViewGroup): MensagemRemetenteViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensRemetentesBinding.inflate(
                    inflater,
                    parent,
                    false
                )
                return MensagemRemetenteViewHolder(itemView)
            }
        }


    }

    class MensagensDestinatariosViewHolder(
        private val binding: ItemMensagensDestinatariosBinding
    ) : ViewHolder(binding.root) {
        fun bind(mensagem: Mensagem){
            binding.textMensagemDestinatario.text = mensagem.mensagem
        }
        companion object {
            fun inflarLayout(parent: ViewGroup): MensagensDestinatariosViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensDestinatariosBinding.inflate(
                    inflater,
                    parent,
                    false
                )
                return MensagensDestinatariosViewHolder(itemView)
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val idUsuarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (idUsuarioLogado == mensagem.idUsuario) {
            Constantes.TIPO_REMETENTE
        } else
            Constantes.TIPO_DESTINATARIO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == Constantes.TIPO_REMETENTE) {
            return MensagemRemetenteViewHolder.inflarLayout(parent)
        }
        return MensagensDestinatariosViewHolder.inflarLayout(parent)
    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when(holder){
            is MensagemRemetenteViewHolder -> holder.bind(mensagem)
            is MensagensDestinatariosViewHolder -> holder.bind(mensagem)
        }
        /*val mensagensRemetenteViewHolder = holder as MensagemRemetenteViewHolder
        mensagensRemetenteViewHolder.bind(mensagem)*/
    }
}
