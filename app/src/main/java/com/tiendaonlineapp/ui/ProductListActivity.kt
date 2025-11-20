package com.tiendaonlineapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tiendaonlineapp.R
import com.tiendaonlineapp.db.DatabaseHelper
import com.tiendaonlineapp.adapters.ProductAdapter


class ProductListActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var recycler: RecyclerView


    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {

            // Permisos concedidos
            getLocation()
        } else {
            // Permisos denegados
            Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        db = DatabaseHelper(this)


        recycler = findViewById(R.id.recyclerProducts)
        recycler.layoutManager = LinearLayoutManager(this)


        loadProducts()

        findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            startActivity(Intent(this, CreateProductActivity::class.java))
        }


        checkLocationPermission()
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {

        val products = db.getAllProducts()


        recycler.adapter = ProductAdapter(products, this)
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getLocation()
        } else {
            // Solicitamos los permisos
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission") // La verificación se hace en checkLocationPermission
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude


                    Toast.makeText(
                        this,
                        "Ubicación: Latitud $lat, Longitud $lon",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    Toast.makeText(this, "Ubicación no disponible.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}