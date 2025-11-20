package com.tiendaonlineapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tiendaonlineapp.R
import com.tiendaonlineapp.db.DatabaseHelper

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private var productId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        db = DatabaseHelper(this)

        productId = intent.getIntExtra("id", 0)

        val product = db.getProductById(productId)

        val name = findViewById<TextView>(R.id.txtName)
        val price = findViewById<TextView>(R.id.txtPrice)
        val desc = findViewById<TextView>(R.id.txtDesc)

        name.text = product?.name
        price.text = "$${product?.price}"
        desc.text = product?.description

        // Botón agregar al carrito
        findViewById<Button>(R.id.btnAgregarCarrito).setOnClickListener {
            if (product != null) {
                db.addToCart(product.name, product.price)
                Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: producto nulo", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón editar
        findViewById<Button>(R.id.btnEditar).setOnClickListener {
            val i = Intent(this, EditProductActivity::class.java)
            i.putExtra("id", productId)
            startActivity(i)
        }
    }
}



