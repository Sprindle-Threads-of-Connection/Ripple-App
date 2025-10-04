package com.example.androidprojectmain

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var signupLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize views
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)

        val auth = FirebaseAuth.getInstance()
        // Login button click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { task ->
                        val uid = task.user?.uid
                        if(uid != null){
                            fetchData(uid)
                        }
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        // Example: Go to Home screen
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Signup link click
        signupLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun fetchData(uid: String){
        val database = FirebaseDatabase.getInstance("https://androidprojectmain-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("users")
        myRef.child(uid).get()
            .addOnSuccessListener {
                if(it.exists()){
                    val name = it.child("name").value
                    val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit()
                        .putString("user_id", uid)
                        .putString("name", name.toString())
                        .apply()

                    Toast.makeText(this, "User Data Fetched", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error Fetching User Data", Toast.LENGTH_SHORT).show()
            }

    }
}
