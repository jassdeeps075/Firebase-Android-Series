package com.example.goxos.google_login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.goxos.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class

MainActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            goToHome()
            return
        }
        setContentView(R.layout.activity_main)

        setupGoogleSignIn()

        findViewById<LinearLayout>(R.id.loginWithGoogle).setOnClickListener {
            signIn()
        }
    }

    private fun setupGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false).build()
            ).build()
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->

            if (result.resultCode == RESULT_OK) {
                try {
                    val credential =
                        oneTapClient.getSignInCredentialFromIntent(result.data)

                    val idToken = credential.googleIdToken

                    if (idToken != null) {
                        firebaseAuthWithGoogle(idToken)
                    } else {
                        showError("Google ID Token is null")
                    }

                } catch (e: Exception) {
                    Log.e("GOOGLE_LOGIN", e.message ?: "Error")
                    showError("Sign-in failed")
                }
            }
        }

    private fun signIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val request =
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()

                googleSignInLauncher.launch(request)
            }
            .addOnFailureListener {
                showError("Google Sign-In not available")
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val user = auth.currentUser

                    Toast.makeText(
                        this,
                        "Welcome ${user?.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()

                    goToHome()

                } else {
                    showError(task.exception?.message ?: "Login failed")
                }
            }
    }

    private fun goToHome() {
        //home screen
//        startActivity(Intent(this, HomeActivity::class.java))
//        finish()
    }

    private fun showError(msg: String) {

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        //home screen

        if (auth.currentUser != null) {
            goToHome()
        }
    }

}