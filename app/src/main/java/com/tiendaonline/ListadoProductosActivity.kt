package com.tiendaonline

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiendaonline.data.AppDatabase
import com.tiendaonline.data.CartItemEntity
import com.tiendaonline.data.ProductEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ListadoProductosActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_productos)

        database = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewProducts)

        adapter = ProductAdapter { product ->
            addToCart(product)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val btnCarrito = findViewById<Button>(R.id.btnCarrito)
        btnCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        // Inicializar productos si la base de datos está vacía
        initializeProducts()
        loadProducts()
    }

    private fun initializeProducts() {
        lifecycleScope.launch {
            // Verificar si hay productos usando first() del Flow
            val productList = database.productDao().getAllProducts().first()
            if (productList.isEmpty()) {
                val initialProducts = listOf(
                    ProductEntity(
                        name = "Camisa Casual",
                        description = "Camisa casual de algodón, cómoda y elegante",
                        price = 45000.0,
                        imageResource = "camisa",
                        category = "Camisas"
                    ),
                    ProductEntity(
                        name = "Pantalón Jeans",
                        description = "Pantalón jeans clásico, corte regular",
                        price = 90000.0,
                        imageResource = "pantalon",
                        category = "Pantalones"
                    ),
                    ProductEntity(
                        name = "Zapatos Deportivos",
                        description = "Zapatos deportivos cómodos para uso diario",
                        price = 120000.0,
                        imageResource = "zapato",
                        category = "Calzado"
                    ),
                    ProductEntity(
                        name = "Chaqueta Deportiva",
                        description = "Chaqueta deportiva con capucha, ideal para clima frío",
                        price = 150000.0,
                        imageResource = "camisa",
                        category = "Chaquetas"
                    ),
                    ProductEntity(
                        name = "Vestido Casual",
                        description = "Vestido casual elegante para ocasiones especiales",
                        price = 110000.0,
                        imageResource = "camisa",
                        category = "Vestidos"
                    )
                )
                database.productDao().insertProducts(initialProducts)
            }
        }
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.productDao().getAllProducts().collect { products ->
                    adapter.submitList(products)
                }
            }
        }
    }

    private fun addToCart(product: ProductEntity) {
        lifecycleScope.launch {
            try {
                val existingItem = database.cartItemDao().getCartItemByProductId(product.id)
                if (existingItem != null) {
                    // Si ya existe, aumentar la cantidad
                    val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                    database.cartItemDao().updateCartItem(updatedItem)
                } else {
                    // Si no existe, crear nuevo item
                    val cartItem = CartItemEntity(productId = product.id, quantity = 1)
                    database.cartItemDao().insertCartItem(cartItem)
                }
                Toast.makeText(
                    this@ListadoProductosActivity,
                    "${product.name} agregado al carrito",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ListadoProductosActivity,
                    "Error al agregar al carrito: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class ProductAdapter(
    private val onAddToCart: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var products: List<ProductEntity> = emptyList()

    fun submitList(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imgProduct)
        private val nameView: TextView = itemView.findViewById(R.id.tvProductName)
        private val priceView: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val descriptionView: TextView = itemView.findViewById(R.id.tvProductDescription)
        private val addButton: Button = itemView.findViewById(R.id.btnAddToCart)

        fun bind(product: ProductEntity) {
            nameView.text = product.name
            priceView.text = "$${String.format("%.0f", product.price)}"
            descriptionView.text = product.description

            // Cargar imagen desde recursos
            val resourceId = itemView.context.resources.getIdentifier(
                product.imageResource,
                "drawable",
                itemView.context.packageName
            )
            if (resourceId != 0) {
                imageView.setImageResource(resourceId)
            }

            addButton.setOnClickListener {
                onAddToCart(product)
            }
        }
    }
}
