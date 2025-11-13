package com.tiendaonline.database

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tienda_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
