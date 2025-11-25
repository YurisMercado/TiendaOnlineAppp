package com.tiendaonlineapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tiendaonlineapp.R
import com.tiendaonlineapp.models.Product
import com.tiendaonlineapp.ui.ProductDetailActivity


class ProductAdapter(
    private val list: List<Product>,
    private val context: Context
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.txtName)
        val price: TextView = v.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val p = list[pos]

        holder.name.text = p.name
        holder.price.text = "$${p.price}"

        holder.itemView.setOnClickListener {
            val i = Intent(context, ProductDetailActivity::class.java)
            i.putExtra("id", p.id)
            context.startActivity(i)
        }
    }
}


