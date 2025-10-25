package com.tiendaonline

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CarritoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val listViewCarrito = findViewById<ListView>(R.id.listViewCarrito)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ListadoProductosActivity.carrito)
        listViewCarrito.adapter = adapter
    }
}
