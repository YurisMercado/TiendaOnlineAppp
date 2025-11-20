package com.tiendaonlineapp.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tiendaonlineapp.R
import com.tiendaonlineapp.db.DatabaseHelper

class CreateProductActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)

        db = DatabaseHelper(this)

        val name = findViewById<EditText>(R.id.editName)
        val price = findViewById<EditText>(R.id.editPrice)
        val desc = findViewById<EditText>(R.id.editDescription)

        val btn = findViewById<Button>(R.id.btnSave)

        btn.setOnClickListener {
            db.addProduct(
                name.text.toString(),
                price.text.toString().toDouble(),
                desc.text.toString()
            )

            Toast.makeText(this, "Guardado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
