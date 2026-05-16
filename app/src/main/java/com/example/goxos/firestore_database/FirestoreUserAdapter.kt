package com.example.goxos.firestore_database

import com.example.goxos.realtime_database.User


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goxos.R

class FirestoreUserAdapter(
    private val list: ArrayList<UserData>,
    private val listener: UserClickListener
) : RecyclerView.Adapter<FirestoreUserAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.nameTv)
        val emailTv: TextView = itemView.findViewById(R.id.emailTv)

        val deleteIv: ImageView = itemView.findViewById(R.id.deleteIv)

        val editIv: ImageView = itemView.findViewById(R.id.editIv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.nameTv.text = user.name
        holder.emailTv.text = user.email

        holder.deleteIv.setOnClickListener {
            listener.onDeleteClick(user)
        }
        holder.editIv.setOnClickListener {
            listener.onUpdateClick(user)
        }
    }

    override fun getItemCount(): Int = list.size
}