package com.tiendaonlineapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.tiendaonlineapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnProductos = findViewById<Button>(R.id.btnProductos)
        val btnCarrito = findViewById<Button>(R.id.btnCarrito)

        btnProductos.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        btnCarrito.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}
