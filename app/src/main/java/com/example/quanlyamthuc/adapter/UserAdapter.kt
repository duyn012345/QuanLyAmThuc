package com.example.quanlyamthuc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlyamthuc.databinding.ItemUserBinding
import com.example.quanlyamthuc.model.UserModel

class UserAdapter(
    private val userList: MutableList<UserModel>,
    private val onEditClick: (UserModel) -> Unit,
    private val onDeleteClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserModel) {
            binding.userNameTextView.text = user.name ?: "No Name"
            binding.userEmailTextView.text = user.email ?: "No Email"
            binding.userRoleTextView.text = user.role ?: "No Role"

            binding.editButton.setOnClickListener { onEditClick(user) }
            binding.deleteButton.setOnClickListener { onDeleteClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    fun updateList(newList: List<UserModel>) {
        Log.d("UserAdapter", "Before update, userList size: ${userList.size}")
        userList.clear()
        userList.addAll(newList)
        Log.d("UserAdapter", "After update, userList size: ${userList.size}")
        notifyDataSetChanged()
    }
}