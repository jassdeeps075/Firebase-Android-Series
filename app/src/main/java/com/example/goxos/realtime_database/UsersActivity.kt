package com.example.goxos.realtime_database

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goxos.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersActivity : AppCompatActivity() {

    private lateinit var nameEt: EditText
    private lateinit var emailEt: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: AppCompatButton
    private lateinit var list: ArrayList<User>
    private lateinit var adapter: UserAdapter
    val ref = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        initViews()
        fetchUsers()
        onClicks()
    }

    private fun onClicks() {
        addBtn.setOnClickListener {
            val name = nameEt.text.toString().trim()
            val email = emailEt.text.toString().trim()

            if (name.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show()
            } else if (email.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            } else {
                addUser(name, email)
            }
        }
    }


    //  Initialize
    private fun initViews() {
        nameEt = findViewById(R.id.nameEt)
        emailEt = findViewById(R.id.emailEt)
        recyclerView = findViewById(R.id.recyclerView)
        addBtn = findViewById(R.id.addBtn)

        recyclerView.layoutManager = LinearLayoutManager(this)

        list = ArrayList()
        adapter = UserAdapter(list, object : OnItemClick{
            override fun onDelete(user: User) {
                user.id?.let { deleteUser(it) }
            }

            override fun onUpdate(user: User) {
               showUpdateDialog(user)
            }

        })

        recyclerView.adapter = adapter
    }

    private fun addUser(name: String, email: String) {

        val id = ref.push().key ?: return

        val user = User(id, name, email)

        ref.child(id).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
                nameEt.text.clear()
                emailEt.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchUsers() {

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) list.add(user)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun updateUser(user: User) {

        val id = user.id ?: return

        ref.child(id).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteUser(userId: String) {

        ref.child(userId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUpdateDialog(user: User) {

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

                    updateUser(updatedUser)
                } else {
                    Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}