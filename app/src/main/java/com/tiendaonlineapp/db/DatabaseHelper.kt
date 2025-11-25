package com.tiendaonlineapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tiendaonlineapp.models.CartItem
import com.tiendaonlineapp.models.Product
import com.tiendaonlineapp.models.User


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "delineation.db", null, 1) { // <-- INICIO DE LA CLASE

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla usuarios
        db.execSQL(
            """
        CREATE TABLE users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            email TEXT,
            password TEXT
        );
        """
        )

        // Tabla productos
        db.execSQL(
            """
        CREATE TABLE products (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            price REAL,
            description TEXT
        );
        """
        )

        // Tabla carrito
        db.execSQL(
            """
        CREATE TABLE cart (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT,
            price REAL
        );
        """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS cart")
        onCreate(db)
    }

    // --- LOGIN/REGISTER ---

    fun login(email: String, password: String): User? {
        val db = readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        if (c.moveToFirst()) {
            val u = User(
                id = c.getInt(0),
                email = c.getString(1),
                password = c.getString(2)
            )
            c.close()
            return u
        }

        c.close()
        return null
    }

    fun register(email: String, password: String): Boolean {
        val cv = ContentValues()
        cv.put("email", email)
        cv.put("password", password)
        return writableDatabase.insert("users", null, cv) > 0
    }

    // --- CRUD PRODUCTOS ---

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val c = readableDatabase.rawQuery("SELECT * FROM products", null)

        if (c.moveToFirst()) {
            do {
                list.add(
                    Product(
                        id = c.getInt(0),
                        name = c.getString(1),
                        price = c.getDouble(2),
                        description = c.getString(3)
                    )
                )
            } while (c.moveToNext())
        }

        c.close()
        return list
    }
    fun addProduct(name: String, price: Double, description: String): Boolean {
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("price", price)
        cv.put("description", description)
        // 'writableDatabase' ahora es accesible porque la función está dentro de la clase
        return writableDatabase.insert("products", null, cv) > 0
    }
    fun getProductById(id: Int): Product? {
        val c = readableDatabase.rawQuery("SELECT * FROM products WHERE id=?", arrayOf(id.toString()))

        if (c.moveToFirst()) {
            val p = Product(
                id = c.getInt(0),
                name = c.getString(1),
                price = c.getDouble(2),
                description = c.getString(3)
            )
            c.close()
            return p
        }

        c.close()
        return null
    }

    fun updateProduct(id: Int, name: String, price: Double, description: String): Boolean {
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("price", price)
        cv.put("description", description)

        return writableDatabase.update("products", cv, "id=?", arrayOf(id.toString())) > 0
    }

    fun deleteProduct(id: Int): Boolean {
        return writableDatabase.delete("products", "id=?", arrayOf(id.toString())) > 0
    }

    // --- CARRITO ---

    // Función addToCart original y correcta (NO USAR LA DUPLICADA)
    fun addToCart(name: String, price: Double): Boolean {
        val cv = ContentValues()
        cv.put("name", name)
        cv.put("price", price)
        return writableDatabase.insert("cart", null, cv) > 0
    }

    fun deleteCartItem(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("cart", "id=?", arrayOf(id.toString()))
        return result > 0
    }
    // ELIMINAR UN PRODUCTO DEL CARRITO
    fun deleteFromCart(id: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete("cart", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }


    fun getCartItems(): List<CartItem> {
        val list = mutableListOf<CartItem>()
        val c = readableDatabase.rawQuery("SELECT * FROM cart", null)

        if (c.moveToFirst()) {
            do {
                list.add(
                    CartItem(
                        id = c.getInt(0),
                        name = c.getString(1),
                        price = c.getDouble(2)
                    )
                )
            } while (c.moveToNext())
        }

        c.close()
        return list
    }
}