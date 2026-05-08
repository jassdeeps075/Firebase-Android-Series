package com.example.goxos.email_password_login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goxos.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEt: EditText
    private lateinit var passwordEt: EditText

    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        emailEt = findViewById(R.id.emailEt)
        passwordEt = findViewById(R.id.passwordEt)
        loginBtn = findViewById(R.id.loginBtn)
        signUpBtn = findViewById(R.id.signUpBtn)

        loginBtn.setOnClickListener {
            authenticateUser(emailEt.text.toString().trim(),passwordEt.text.toString().trim())
        }

        signUpBtn.setOnClickListener {
            registerUser(emailEt.text.toString().trim(),passwordEt.text.toString().trim())
        }
    }

    private fun authenticateUser(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {
            showToast("Enter email & password")
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onAuthSuccess("Login Successful")
            }
            .addOnFailureListener { error ->
                handleLoginFailure(error)
            }
    }

    private fun handleLoginFailure(error: Exception) {

        when (error) {

            is FirebaseAuthInvalidUserException -> {
                showToast("Account does not exist")
            }

            is FirebaseAuthInvalidCredentialsException -> {
                showToast("Wrong password")
            }

            else -> {
                showToast("Login Failed: ${error.message}")
            }
        }
    }
    private fun registerUser(email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onAuthSuccess("Registered Successfully")
            }
            .addOnFailureListener { error ->

                if (error is FirebaseAuthUserCollisionException) {
                    showToast("Account already exists")
                } else {
                    showToast("Signup Failed: ${error.message}")
                }
            }
    }

    private fun onAuthSuccess(message: String) {
        showToast(message)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null){
            //next screen
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }


}