package com.example.quanlyamthuc.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlyamthuc.LoginActivity
import com.example.quanlyamthuc.adapter.UserAdapter
import com.example.quanlyamthuc.databinding.DialogUserBinding
import com.example.quanlyamthuc.databinding.FragmentUserBinding
import com.example.quanlyamthuc.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<UserModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        // Khởi tạo RecyclerView và Adapter
        userAdapter = UserAdapter(userList, { user -> showEditDialog(user) }, { user -> deleteUser(user) })
        binding.userRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.userRecyclerView.adapter = userAdapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            firestore.collection("nguoidung").document(currentUser.uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val role = snapshot.getString("role") ?: "user"
                        if (role == "admin") {
                            fetchUsers()
                        } else {
                            Toast.makeText(context, "Chỉ admin mới có thể xem danh sách người dùng", Toast.LENGTH_SHORT).show()
                            userList.clear()
                            userAdapter.updateList(userList)
                        }
                    } else {
                        Toast.makeText(context, "Thông tin người dùng không tồn tại. Vui lòng liên hệ quản trị viên.", Toast.LENGTH_LONG).show()
                        userList.clear()
                        userAdapter.updateList(userList)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Không thể kiểm tra vai trò: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("UserFragment", "Failed to check role: ${e.message}")
                }
        } else {
            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            userList.clear()
            userAdapter.updateList(userList)
            activity?.let {
                startActivity(Intent(it, LoginActivity::class.java))
                it.finish()
            }
        }

        binding.addUserButton.setOnClickListener {
            showAddDialog()
        }
    }

    private fun fetchUsers() {
        firestore.collection("nguoidung").get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                Log.d("UserFragment", "Before clear, userList size: ${userList.size}")
                userList.clear()
                Log.d("UserFragment", "After clear, userList size: ${userList.size}")
                for (document in querySnapshot.documents) {
                    try {
                        val user = document.toObject(UserModel::class.java)
                        user?.let {
                            it.userId = document.id
                            userList.add(it)
                            Log.d("UserFragment", "Added user: ${it.name}, ${it.email}, ${it.role}, userId: ${it.userId}")
                        } ?: run {
                            Log.e("UserFragment", "Failed to parse user from document: ${document.id}")
                        }
                    } catch (e: Exception) {
                        Log.e("UserFragment", "Error parsing document ${document.id}: ${e.message}")
                    }
                }
                Log.d("UserFragment", "Before update adapter, userList size: ${userList.size}")
                userAdapter.updateList(userList.toList())
                Log.d("UserFragment", "Total users fetched: ${userList.size}")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Lỗi khi lấy dữ liệu: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("UserFragment", "Failed to fetch users: ${e.message}")
                userList.clear()
                userAdapter.updateList(userList)
            }
    }

    private fun showAddDialog() {
        val dialogBinding = DialogUserBinding.inflate(LayoutInflater.from(context))
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add User")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val name = dialogBinding.edtname.text.toString().trim()
                val email = dialogBinding.edtemail.text.toString().trim()
                val role = dialogBinding.edtrole.text.toString().trim()

                if (name.isBlank() || email.isBlank() || role.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newUser = UserModel(
                    name = name,
                    email = email,
                    role = role
                )
                firestore.collection("nguoidung").add(newUser)
                    .addOnSuccessListener { documentReference ->
                        val userId = documentReference.id
                        newUser.userId = userId
                        firestore.collection("nguoidung").document(userId).set(newUser)
                        Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show()
                        fetchUsers()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to add user: ${e.message}", Toast.LENGTH_LONG).show()
                        Log.e("UserFragment", "Failed to add user: ${e.message}")
                    }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showEditDialog(user: UserModel) {
        val dialogBinding = DialogUserBinding.inflate(LayoutInflater.from(context))
        dialogBinding.edtname.setText(user.name)
        dialogBinding.edtemail.setText(user.email)
        dialogBinding.edtrole.setText(user.role)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit User")
            .setView(dialogBinding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = dialogBinding.edtname.text.toString().trim()
                val email = dialogBinding.edtemail.text.toString().trim()
                val role = dialogBinding.edtrole.text.toString().trim()

                if (name.isBlank() || email.isBlank() || role.isBlank()) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updatedUser = UserModel(
                    userId = user.userId,
                    name = name,
                    email = email,
                    role = role
                )
                user.userId?.let { userId ->
                    firestore.collection("nguoidung").document(userId).set(updatedUser)
                        .addOnSuccessListener {
                            Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                            fetchUsers()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to update user: ${e.message}", Toast.LENGTH_LONG).show()
                            Log.e("UserFragment", "Failed to update user: ${e.message}")
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun deleteUser(user: UserModel) {
        user.userId?.let { userId ->
            firestore.collection("nguoidung").document(userId).delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    fetchUsers()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to delete user: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("UserFragment", "Failed to delete user: ${e.message}")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}