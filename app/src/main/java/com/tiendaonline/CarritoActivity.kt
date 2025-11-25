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
import com.tiendaonline.data.CartItemEntity
import com.tiendaonline.data.ProductEntity
import com.tiendaonline.utils.LocationHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var locationHelper: LocationHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var btnGetLocation: Button
    private lateinit var btnCheckout: Button
    private lateinit var adapter: CartAdapter

    private var deliveryLocation: Location? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getDeliveryLocation()
            }
            else -> {
                Toast.makeText(
                    this,
                    "Permiso de ubicación requerido para la entrega",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        database = AppDatabase.getDatabase(this)
        locationHelper = LocationHelper(this)

        recyclerView = findViewById(R.id.recyclerViewCart)
        tvTotal = findViewById(R.id.tvTotal)
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnCheckout = findViewById(R.id.btnCheckout)

        adapter = CartAdapter(
            onQuantityChange = { cartItem, newQuantity ->
                updateCartItemQuantity(cartItem, newQuantity)
            },
            onRemove = { cartItem ->
                removeFromCart(cartItem)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnGetLocation.setOnClickListener {
            if (checkLocationPermission()) {
                getDeliveryLocation()
            } else {
                requestLocationPermission()
            }
        }

        btnCheckout.setOnClickListener {
            checkout()
        }

        loadCartItems()
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

    private fun getDeliveryLocation() {
        lifecycleScope.launch {
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    deliveryLocation = location
                    val address = locationHelper.getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    )
                    tvDeliveryAddress.text = address ?: "Lat: ${location.latitude}, Lng: ${location.longitude}"
                    Toast.makeText(
                        this@CarritoActivity,
                        "Dirección de entrega actualizada",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@CarritoActivity,
                        "No se pudo obtener la ubicación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CarritoActivity,
                    "Error al obtener ubicación: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadCartItems() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.cartItemDao().getAllCartItems().collect { cartItems ->
                    // Obtener los productos correspondientes
                    val cartItemsWithProducts = mutableListOf<CartItemWithProduct>()
                    cartItems.forEach { cartItem ->
                        val product = database.productDao().getProductById(cartItem.productId)
                        product?.let {
                            cartItemsWithProducts.add(CartItemWithProduct(cartItem, it))
                        }
                    }
                    adapter.submitList(cartItemsWithProducts)
                    updateTotal(cartItemsWithProducts)
                }
            }
        }
    }

    private suspend fun updateTotal(cartItems: List<CartItemWithProduct>) {
        val total = cartItems.sumOf { it.cartItem.quantity * it.product.price }
        tvTotal.text = "Total: $${String.format("%.0f", total)}"
    }

    private fun updateCartItemQuantity(cartItem: CartItemEntity, newQuantity: Int) {
        lifecycleScope.launch {
            if (newQuantity <= 0) {
                database.cartItemDao().deleteCartItem(cartItem)
            } else {
                val updatedItem = cartItem.copy(quantity = newQuantity)
                database.cartItemDao().updateCartItem(updatedItem)
            }
        }
    }

    private fun removeFromCart(cartItem: CartItemEntity) {
        lifecycleScope.launch {
            database.cartItemDao().deleteCartItem(cartItem)
            Toast.makeText(
                this@CarritoActivity,
                "Producto eliminado del carrito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkout() {
        lifecycleScope.launch {
            val cartItems = database.cartItemDao().getAllCartItems().first()
            if (cartItems.isEmpty()) {
                Toast.makeText(
                    this@CarritoActivity,
                    "El carrito está vacío",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            if (deliveryLocation == null) {
                Toast.makeText(
                    this@CarritoActivity,
                    "Por favor, seleccione una dirección de entrega",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            // Guardar la ubicación de entrega en la base de datos
            val address = locationHelper.getAddressFromLocation(
                deliveryLocation!!.latitude,
                deliveryLocation!!.longitude
            )
            val locationEntity = com.tiendaonline.data.LocationEntity(
                latitude = deliveryLocation!!.latitude,
                longitude = deliveryLocation!!.longitude,
                address = "Entrega: $address"
            )
            database.locationDao().insertLocation(locationEntity)

            // Limpiar el carrito
            database.cartItemDao().deleteAllCartItems()

            Toast.makeText(
                this@CarritoActivity,
                "¡Compra realizada exitosamente! Dirección de entrega guardada.",
                Toast.LENGTH_LONG
            ).show()

            finish()
        }
    }
}

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

class CartAdapter(
    private val onQuantityChange: (CartItemEntity, Int) -> Unit,
    private val onRemove: (CartItemEntity) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private var cartItems: List<CartItemWithProduct> = emptyList()

    fun submitList(newItems: List<CartItemWithProduct>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CartViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.tvCartItemName)
        private val priceView: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        private val quantityView: TextView = itemView.findViewById(R.id.tvQuantity)
        private val totalView: TextView = itemView.findViewById(R.id.tvCartItemTotal)
        private val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        private val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
        private val btnRemove: Button = itemView.findViewById(R.id.btnRemove)

        fun bind(item: CartItemWithProduct) {
            nameView.text = item.product.name
            priceView.text = "$${String.format("%.0f", item.product.price)} c/u"
            quantityView.text = item.cartItem.quantity.toString()
            val total = item.cartItem.quantity * item.product.price
            totalView.text = "Total: $${String.format("%.0f", total)}"

            btnIncrease.setOnClickListener {
                onQuantityChange(item.cartItem, item.cartItem.quantity + 1)
            }

            btnDecrease.setOnClickListener {
                onQuantityChange(item.cartItem, item.cartItem.quantity - 1)
            }

            btnRemove.setOnClickListener {
                onRemove(item.cartItem)
            }
        }
    }
}
