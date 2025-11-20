package com.tiendaonlineapp.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tiendaonlineapp.databinding.ActivityDetailProductBinding
import com.tiendaonlineapp.db.DatabaseHelper

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var db: DatabaseHelper
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        productId = intent.getIntExtra("id", 0)

        val product = db.getProductById(productId)

        if (product == null) {
            Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos en pantalla
        binding.name.text = product.name
        binding.price.text = "$${product.price}"
        binding.description.text = product.description

        // Botón Editar
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("id", productId)
            startActivity(intent)
        }

        // Botón Agregar al carrito
        binding.btnAddCart.setOnClickListener {
            val result = db.addToCart(product.name, product.price)

            if (result) {
                Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón ubicación
        binding.btnLocation.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=Tienda Online")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }
}
