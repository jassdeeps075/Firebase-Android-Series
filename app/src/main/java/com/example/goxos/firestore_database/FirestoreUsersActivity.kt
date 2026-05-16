package com.example.goxos.firestore_database

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goxos.R
import com.example.goxos.realtime_database.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreUsersActivity : AppCompatActivity(), UserClickListener {

    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var addBtn: AppCompatButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<UserData>
    private lateinit var adapter: FirestoreUserAdapter

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_users)

        initViews()
        setupRecycler()
        loadUsers()

        addBtn.setOnClickListener {
            addUser()
        }
    }

    private fun setupRecycler() {
        list = ArrayList()
        adapter = FirestoreUserAdapter(list, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun initViews() {
        nameEt = findViewById(R.id.nameEt)
        emailEt = findViewById(R.id.emailEt)
        addBtn = findViewById(R.id.addBtn)
        recyclerView = findViewById(R.id.recyclerView)
    }


    private fun addUser() {

        val name = nameEt.text.toString().trim()
        val email = emailEt.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val docRef = db.collection("users").document()

        val user = UserData(
            id = docRef.id,
            name = name,
            email = email
        )

        docRef.set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUsers() {

        db.collection("users")
            .addSnapshotListener { value, error ->

                if (error != null) {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                list.clear()

                for (doc in value!!.documents) {
                    val user = doc.toObject(UserData::class.java)
                    if (user != null) {
                        list.add(user)
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }

    private fun clearFields() {
        nameEt.text.clear()
        emailEt.text.clear()
    }

    override fun onUpdateClick(user: UserData) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_update_user, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etEmail = dialogView.findViewById<EditText>(R.id.etEmail)

        etName.setText(user.name)
        etEmail.setText(user.email)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Update User")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->

                val newName = etName.text.toString().trim()
                val newEmail = etEmail.text.toString().trim()

                if (newName.isNotEmpty() && newEmail.isNotEmpty()) {

                    val updatedUser = user.copy(
                        name = newName,
                        email = newEmail
                    )

                    db.collection("users")
                        .document(user.id ?: "")
                        .set(updatedUser)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    override fun onDeleteClick(user: UserData) {
        AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->

                db.collection("users")
                    .document(user.id ?: "")
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

}