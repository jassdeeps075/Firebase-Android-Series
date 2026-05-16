package com.example.goxos.firestore_database

interface UserClickListener {
    fun onUpdateClick(user: UserData)
    fun onDeleteClick(user: UserData)

}