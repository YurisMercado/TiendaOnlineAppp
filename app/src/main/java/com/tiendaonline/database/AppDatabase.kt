package com.tiendaonline.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tiendaonline.database.productDao
import com.tiendaonline.database.Product

@Database(entities = [Product::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): productDao
}
