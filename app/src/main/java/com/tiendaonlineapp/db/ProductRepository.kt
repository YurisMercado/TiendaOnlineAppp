package com.tiendaonlineapp.database

import android.content.Context
import com.tiendaonlineapp.models.Product

class ProductRepository(context: Context) {

    private val dao = ProductDao(context)

    fun addProduct(product: Product) = dao.insert(product)

    fun getProducts() = dao.getAll()

    fun updateProduct(product: Product) = dao.update(product)

    fun deleteProduct(id: Int) = dao.delete(id)
}
