package com.tiendaonline

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tiendaonline.database.DatabaseBuilder
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val txtResumen = findViewById<TextView>(R.id.txtResumen)

        val db = DatabaseBuilder.getInstance(this)

        lifecycleScope.launch {
            val productos = db.appDao().getAllProducts()
            var total = 0.0
            val listado = StringBuilder()

            for (p in productos) {
                listado.append("${p.name} - $${p.price}\n")
                total += p.price
            }

            listado.append("\nTOTAL: $${total}")
            txtResumen.text = listado.toString()
        }
    }
}
