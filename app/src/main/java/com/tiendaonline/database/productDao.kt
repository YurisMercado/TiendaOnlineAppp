package com.tiendaonline.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface productDao {

    @Insert
    suspend fun insert(product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
}
