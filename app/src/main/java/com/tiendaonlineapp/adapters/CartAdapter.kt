package com.tiendaonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tiendaonlineapp.R
import com.tiendaonlineapp.db.DatabaseHelper
import com.tiendaonlineapp.models.CartItem

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val db: DatabaseHelper,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.txtCartName)
        val price: TextView = itemView.findViewById(R.id.txtCartPrice)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.name
        holder.price.text = "$${item.price}"

        holder.btnDelete.setOnClickListener {
            onDelete(item.id) // Se lo manda al Activity para eliminar
        }
    }

    override fun getItemCount(): Int = items.size
}


