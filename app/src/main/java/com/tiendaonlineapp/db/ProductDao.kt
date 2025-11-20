package com.tiendaonlineapp.database

import android.content.ContentValues
import android.content.Context
import com.tiendaonlineapp.db.DatabaseHelper
import com.tiendaonlineapp.models.Product

class ProductDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insert(product: Product): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        values.put("name", product.name)
        values.put("price", product.price)
        values.put("description", product.description)

        return db.insert("products", null, values)
    }

    fun getAll(): ArrayList<Product> {
        val list = ArrayList<Product>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Product(
                        id = cursor.getInt(0),
                        name = cursor.getString(1),
                        price = cursor.getDouble(2),
                        description = cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    fun update(product: Product): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues()

        values.put("name", product.name)
        values.put("price", product.price)
        values.put("description", product.description)

        return db.update("products", values, "id=?", arrayOf(product.id.toString()))
    }

    fun delete(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("products", "id=?", arrayOf(id.toString()))
    }
}
