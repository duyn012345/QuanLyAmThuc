package com.example.quanlyamthuc.adapter

import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.quanlyamthuc.databinding.ItemBlockBinding
import com.example.quanlyamthuc.model.BlockModel
import com.example.quanlyamthuc.adapter.BlockAdapter
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlyamthuc.R
import com.google.firebase.database.FirebaseDatabase

class BlockAdapter (
    private var blockList: List<BlockModel>,
    private val onDeleteClick: (BlockModel) -> Unit
) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val binding = ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.bind(blockList[position])
    }

    override fun getItemCount(): Int = blockList.size

    inner class BlockViewHolder(val binding: ItemBlockBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(block: BlockModel) {
            binding.txtTenMa.text = block.tenmonan
            binding.txtNoiDung.text = block.noidung
            binding.txtNgayDang.text = "Đăng ngày: ${block.created_at}"
            binding.txtTinh.text = block.tinhthanh
            binding.txtLike.text = block.so_like

            if (!block.hinhanh_ma.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(block.hinhanh_ma)
                    .into(binding.imageBlock)
            } else {
                binding.imageBlock.setImageResource(R.drawable.banner1)
            }

            binding.btnDelete.setOnClickListener {
                val context = binding.root.context
                AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa bài đăng này không?")
                    .setPositiveButton("Xóa") { _, _ ->
                                onDeleteClick(block)

                    }
                    .setNegativeButton("Hủy", null)
                    .show()
                }
            }
        }
    fun updateList(newList: List<BlockModel>) {
        this.blockList = newList.toMutableList()
        notifyDataSetChanged()
    }
}
