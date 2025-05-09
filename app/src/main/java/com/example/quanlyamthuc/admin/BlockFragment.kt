package com.example.quanlyamthuc.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlyamthuc.R
import com.example.quanlyamthuc.model.BlockModel
import com.example.quanlyamthuc.adapter.BlockAdapter
import com.example.quanlyamthuc.databinding.FragmentBlockBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.*

class BlockFragment : Fragment() {

        private lateinit var binding: FragmentBlockBinding
        private lateinit var databaseRef: DatabaseReference
        private val fullList = mutableListOf<BlockModel>()  // Danh sách gốc
        private val blockList = mutableListOf<BlockModel>() // Danh sách hiển thị
        private lateinit var adapter: BlockAdapter

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentBlockBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            adapter = BlockAdapter(blockList) { block ->
                val key = block.idbd ?: return@BlockAdapter
                FirebaseDatabase.getInstance().getReference("2/data").child(key)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Đã xóa thành công", Toast.LENGTH_SHORT).show()
                        fullList.removeIf { it.idbd == key }
                        blockList.removeIf { it.idbd == key }
                        adapter.updateList(blockList)
                       // adapter.updateList(blockList)  // Cập nhật giao diện
                        binding.txtTongBaiDang.text = "Tổng bài đăng: ${blockList.size}"
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                    }
            }
            binding.recyclerBaiDang.layoutManager = GridLayoutManager(context, 2) // 3 cột
            binding.recyclerBaiDang.adapter = adapter

            // Tìm kiếm
            binding.edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    val searchText = s.toString().trim()
                    val filtered = if (searchText.isEmpty()) {
                        fullList
                    } else {
                        fullList.filter {
                                     it.tenmonan?.contains(searchText, ignoreCase = true) == true ||
                                    it.noidung?.contains(searchText, ignoreCase = true) == true ||
                                    it.tinhthanh?.contains(searchText, ignoreCase = true) == true
                        }
                    }
                    blockList.clear()
                    blockList.addAll(filtered)
                    adapter.updateList(filtered)
                   // binding.recyclerBaiDang.adapter = adapter
                }
            })

            getPostData()
        }

        private fun getPostData() {
            databaseRef = FirebaseDatabase.getInstance().getReference("2/data")

            databaseRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    fullList.clear()
                    blockList.clear()
                    for (blockSnap in snapshot.children) {
                        val block = blockSnap.getValue(BlockModel::class.java)
                        block?.let {
                            fullList.add(it)
                            blockList.add(it)
                        }
                    }
                    adapter.updateList(blockList)
                    binding.txtTongBaiDang.text = "Tổng bài đăng: ${blockList.size}"

                    // Cập nhật lại danh sách bài đăng trong RecyclerView
                   // val updatedList = blockList.filter { }
                   // adapter.updateList(updatedList)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

}
