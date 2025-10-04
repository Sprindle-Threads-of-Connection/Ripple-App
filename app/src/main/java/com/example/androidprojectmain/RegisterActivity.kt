package com.example.androidprojectmain

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText

    private lateinit var emailInput: EditText

    private lateinit var passwordInput: EditText

    private lateinit var signUpBtn: Button

    private lateinit var backToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        signUpBtn = findViewById(R.id.signUpButton)
        backToLogin = findViewById(R.id.loginLink)

        signUpBtn.setOnClickListener {
            if(nameInput.text.toString().isEmpty() || emailInput.text.toString().isEmpty() || passwordInput.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        backToLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}