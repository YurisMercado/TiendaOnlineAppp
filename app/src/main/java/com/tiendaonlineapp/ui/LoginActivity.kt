package com.tiendaonlineapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tiendaonlineapp.databinding.ActivityLoginBinding
import com.tiendaonlineapp.db.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        // Botón de Iniciar Sesión
        binding.btnLogin.setOnClickListener {
            validarLogin()
        }

        // Link para ir a Registro
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validarLogin() {

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // ---------- VALIDACIÓN DE EMAIL ----------
        if (email.isEmpty()) {
            binding.etEmail.error = "El correo es obligatorio"
            binding.etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Correo inválido"
            binding.etEmail.requestFocus()
            return
        }

        // ---------- VALIDACIÓN DE CONTRASEÑA ----------
        if (password.isEmpty()) {
            binding.etPassword.error = "La contraseña es obligatoria"
            binding.etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Mínimo 6 caracteres"
            binding.etPassword.requestFocus()
            return
        }

        // ---------- VALIDACIÓN EN BASE DE DATOS ----------
        val user = db.login(email, password)

        if (user != null) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

            // CORREGIDO → DEBE IR A MAINACTIVITY
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }
}
