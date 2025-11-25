package com.tiendaonlineapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiendaonlineapp.R
import com.tiendaonlineapp.adapters.CartAdapter
import com.tiendaonlineapp.db.DatabaseHelper
import com.tiendaonlineapp.models.CartItem

class CartActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: CartAdapter
    private lateinit var recycler: RecyclerView
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        db = DatabaseHelper(this)
        recycler = findViewById(R.id.recyclerCart)

        // Cargar carrito desde la BD
        val itemsFromDb = db.getCartItems()
        cartItems.addAll(itemsFromDb)

        adapter = CartAdapter(cartItems, db) { idToDelete ->
            deleteFromCart(idToDelete)
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
    }

    private fun deleteFromCart(id: Int) {
        val deleted = db.deleteFromCart(id)

        if (deleted) {
            val itemIndex = cartItems.indexOfFirst { it.id == id }

            if (itemIndex != -1) {
                cartItems.removeAt(itemIndex)
                adapter.notifyItemRemoved(itemIndex)
            }

            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
    }
}
