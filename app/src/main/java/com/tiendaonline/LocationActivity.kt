package com.tiendaonline

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiendaonline.data.AppDatabase
import com.tiendaonline.data.LocationEntity
import com.tiendaonline.utils.LocationHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LocationActivity : AppCompatActivity() {
    private lateinit var locationHelper: LocationHelper
    private lateinit var database: AppDatabase
    private lateinit var tvCurrentLocation: TextView
    private lateinit var btnGetLocation: Button
    private lateinit var btnSaveLocation: Button
    private lateinit var btnClearLocations: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter

    private var currentLocation: Location? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getCurrentLocation()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Location permission is required to use this feature",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationHelper = LocationHelper(this)
        database = AppDatabase.getDatabase(this)

        tvCurrentLocation = findViewById(R.id.tvCurrentLocation)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnSaveLocation = findViewById(R.id.btnSaveLocation)
        btnClearLocations = findViewById(R.id.btnClearLocations)
        recyclerView = findViewById(R.id.recyclerViewLocations)

        adapter = LocationAdapter { location ->
            deleteLocation(location)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnGetLocation.setOnClickListener {
            if (checkLocationPermission()) {
                getCurrentLocation()
            } else {
                requestLocationPermission()
            }
        }

        btnSaveLocation.setOnClickListener {
            if (currentLocation != null) {
                saveLocationToDatabase(currentLocation!!)
            } else {
                Toast.makeText(this, "Please get your location first", Toast.LENGTH_SHORT).show()
            }
        }

        btnClearLocations.setOnClickListener {
            clearAllLocations()
        }

        loadSavedLocations()
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun getCurrentLocation() {
        lifecycleScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    currentLocation = location
                    val address = locationHelper.getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    )
                    updateLocationDisplay(location, address)
                } else {
                    Toast.makeText(
                        this@LocationActivity,
                        "Unable to get location. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LocationActivity,
                    "Error getting location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateLocationDisplay(location: Location, address: String?) {
        val locationText = buildString {
            append("Latitude: ${location.latitude}\n")
            append("Longitude: ${location.longitude}\n")
            append("Accuracy: ${location.accuracy}m\n")
            if (address != null) {
                append("Address: $address")
            } else {
                append("Address: Not available")
            }
        }
        tvCurrentLocation.text = locationText
    }

    private fun saveLocationToDatabase(location: Location) {
        lifecycleScope.launch {
            try {
                val address = locationHelper.getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                val locationEntity = LocationEntity(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    address = address
                )
                database.locationDao().insertLocation(locationEntity)
                Toast.makeText(
                    this@LocationActivity,
                    "Location saved to database",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LocationActivity,
                    "Error saving location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadSavedLocations() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.locationDao().getAllLocations().collect { locations ->
                    adapter.submitList(locations)
                }
            }
        }
    }

    private fun deleteLocation(location: LocationEntity) {
        lifecycleScope.launch {
            try {
                database.locationDao().deleteLocation(location)
                Toast.makeText(
                    this@LocationActivity,
                    "Location deleted",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LocationActivity,
                    "Error deleting location: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearAllLocations() {
        lifecycleScope.launch {
            try {
                database.locationDao().deleteAllLocations()
                Toast.makeText(
                    this@LocationActivity,
                    "All locations cleared",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LocationActivity,
                    "Error clearing locations: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

// Simple adapter for displaying locations
class LocationAdapter(
    private val onDeleteClick: (LocationEntity) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {
    private var locations: List<LocationEntity> = emptyList()

    fun submitList(newLocations: List<LocationEntity>) {
        locations = newLocations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): LocationViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int = locations.size

    inner class LocationViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(location: LocationEntity) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(location.timestamp))

            text1.text = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            text2.text = "${location.address ?: "No address"} - $dateString"

            itemView.setOnClickListener {
                onDeleteClick(location)
            }
        }
    }
}

