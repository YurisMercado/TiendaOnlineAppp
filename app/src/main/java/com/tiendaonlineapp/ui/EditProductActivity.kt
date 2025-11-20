package com.tiendaonlineapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.tiendaonlineapp.databinding.ActivityEditProductBinding
import com.tiendaonlineapp.db.DatabaseHelper
import com.tiendaonlineapp.models.Product

class EditProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private lateinit var db: DatabaseHelper
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        // Recibir ID del producto
        productId = intent.getIntExtra("id", 0)

        if (productId == 0) {
            Toast.makeText(this, "Error al cargar producto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadProduct()

        binding.btnUpdate.setOnClickListener {
            updateProduct()
        }

        binding.btnDelete.setOnClickListener {
            deleteProduct()
        }
    }

    private fun loadProduct() {
        val product = db.getProductById(productId)

        if (product != null) {
            binding.editName.setText(product.name)
            binding.editPrice.setText(product.price.toString())
            binding.editDescription.setText(product.description)
        }
    }

    private fun updateProduct() {
        val name = binding.editName.text.toString()
        // Uso de .toDoubleOrNull() para manejo seguro de precios
        val price = binding.editPrice.text.toString().toDoubleOrNull()
        val description = binding.editDescription.text.toString()

        if (name.isEmpty() || price == null || description.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // CORRECCIÓN: Llamada a updateProduct con los 4 argumentos separados
        val updated = db.updateProduct(
            productId,   // ID
            name,        // Nombre
            price,       // Precio
            description  // Descripción
        )

        if (updated) {
            Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProduct() {
        val deleted = db.deleteProduct(productId)

        if (deleted) {
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show()
        }
    }
}