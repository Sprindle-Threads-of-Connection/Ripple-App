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
import com.example.androidprojectmain.Data.dbData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        val auth = FirebaseAuth.getInstance()

        signUpBtn.setOnClickListener {
            if(nameInput.text.toString().isEmpty() || emailInput.text.toString().isEmpty() || passwordInput.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { task ->
                        val uid = task.user?.uid
                        if(uid != null){
                            create(uid, nameInput.text.toString(), emailInput.text.toString())
                        }
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                        // Example: Go to Home screen
                        val intent = Intent(this, AuthActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        backToLogin.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun create(uid: String, name: String, email: String){
        val database = FirebaseDatabase.getInstance("https://androidprojectmain-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("users")

        val data = dbData(uid, name, email)
        myRef.child(uid).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data Stored in Database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error Storing Data in Database", Toast.LENGTH_SHORT).show()
            }
    }
}