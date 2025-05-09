package com.example.quanlyamthuc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlyamthuc.R
import com.example.quanlyamthuc.model.ReviewModel
class ReviewAdapter(
    private val context: Context,
    private var reviewList: List<ReviewModel>,
    private val onDeleteClick: (ReviewModel) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var tenNguoiDungMap = mapOf<String, String>()
    private var tenMonAnMap = mapOf<String, String>()

    fun setTenNguoiDungMap(map: Map<String, String>) {
        tenNguoiDungMap = map
        notifyDataSetChanged()
    }

    fun setTenMonAnMap(map: Map<String, String>) {
        tenMonAnMap = map
        notifyDataSetChanged()
    }

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUserIcon: ImageView = itemView.findViewById(R.id.imgUserIcon)
        val txtTenNguoiDung: TextView = itemView.findViewById(R.id.txtTenNguoiDung)
        val txtNgayTao: TextView = itemView.findViewById(R.id.txtNgayTao)
        val imgDanhGia: ImageView = itemView.findViewById(R.id.imgDanhGia)
        val txtNoiDung: TextView = itemView.findViewById(R.id.txtNoiDung)
        val txtSoSao: TextView = itemView.findViewById(R.id.txtSoSao)
         val delButton: ImageView = itemView.findViewById(R.id.delButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]
        val tenNguoiDung = tenNguoiDungMap[review.idnd] ?: "Ẩn danh"
        val tenMonAn = tenMonAnMap[review.idma] ?: "Không rõ"

        holder.txtTenNguoiDung.text =  "$tenNguoiDung (${tenMonAn})"
        holder.txtNoiDung.text = review.noi_dung
        holder.txtSoSao.text = "★".repeat(review.so_sao?.toIntOrNull() ?: 0)
        holder.txtNgayTao.text = "Ngày: ${review.created_at ?: "Không rõ"}"

        Glide.with(context)
            .load(review.avatar_url)
            .placeholder(R.drawable.usernew)
            .into(holder.imgUserIcon)

        Glide.with(context)
            .load(review.hinhanh_danhgia)
            .placeholder(R.drawable.banner1) // đặt placeholder nếu muốn
            .into(holder.imgDanhGia)

        holder.delButton.setOnClickListener {
            onDeleteClick(review)
        }
    }

    override fun getItemCount(): Int = reviewList.size

    fun updateList(newList: List<ReviewModel>) {
        reviewList = newList
        notifyDataSetChanged()
    }
}
