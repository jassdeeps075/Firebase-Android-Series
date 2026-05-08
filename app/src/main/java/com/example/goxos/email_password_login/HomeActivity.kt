package com.example.goxos.email_password_login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goxos.email_password_login.LoginActivity
import com.example.goxos.R
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var emailText: TextView
    private lateinit var logoutBtn: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()

        emailText = findViewById(R.id.emailText)
        logoutBtn = findViewById(R.id.logoutBtn)


        val user = mAuth.currentUser

        if (user != null) {
            emailText.text = "Logged in as: ${user.email}"
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        logoutBtn.setOnClickListener {

            mAuth.signOut()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}