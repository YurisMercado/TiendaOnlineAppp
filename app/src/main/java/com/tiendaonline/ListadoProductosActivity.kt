package com.tiendaonline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ListadoProductosActivity : AppCompatActivity() {

    companion object {
        val carrito = mutableListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_productos)

        val btnCarrito = findViewById<Button>(R.id.btnCarrito)

        val btnAgregarCamisa = findViewById<Button>(R.id.btnAgregarCamisa)
        val btnAgregarPantalon = findViewById<Button>(R.id.btnAgregarpantalon)

        btnAgregarCamisa.setOnClickListener {
            carrito.add("Camisa Casual - $45.000")
        }

        btnAgregarPantalon.setOnClickListener {
            carrito.add("Pantalón Jeans - $90.000")
        }

        btnCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
    }
}
