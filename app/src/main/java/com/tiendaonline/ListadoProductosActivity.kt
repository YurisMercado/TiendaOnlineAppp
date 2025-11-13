package com.tiendaonline

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tiendaonline.database.DatabaseHelper

class ListadoProductosActivity : AppCompatActivity() {

    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_productos)

        val helper = DatabaseHelper(this)
        db = helper.writableDatabase

        val btnCamisa = findViewById<Button>(R.id.btnAgregarCamisa)
        val btnPantalon = findViewById<Button>(R.id.btnAgregarpantalon)
        val btnCarrito = findViewById<Button>(R.id.btnCarrito)
        val btnUbicacion = findViewById<Button>(R.id.btnUbicacion)

        btnCamisa.setOnClickListener {
            db.execSQL("INSERT INTO carrito (nombre, precio) VALUES ('Camisa Casual', 45000)")
            Toast.makeText(this, "Camisa agregada al carrito", Toast.LENGTH_SHORT).show()
        }

        btnPantalon.setOnClickListener {
            db.execSQL("INSERT INTO carrito (nombre, precio) VALUES ('Pantalón Jeans', 90000)")
            Toast.makeText(this, "Pantalón agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        btnCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        btnUbicacion.setOnClickListener {
            startActivity(Intent(this, UbicacionActivity::class.java))
        }
    }
}
