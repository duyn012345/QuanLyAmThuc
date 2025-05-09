package com.example.quanlyamthuc.admin
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlyamthuc.R
import com.example.quanlyamthuc.adapter.ReviewAdapter
import com.example.quanlyamthuc.model.ReviewModel
import com.google.firebase.database.*


class ReviewFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewAdapter: ReviewAdapter
    private var reviewList = mutableListOf<ReviewModel>()
    private lateinit var txtTongSoBaiDang: TextView
    private lateinit var containerThongKeMon: LinearLayout
    private lateinit var searchView: EditText
    private val tenMonAnMap = mutableMapOf<String, String>()
    private val tenNguoiDungMap = mutableMapOf<String, String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerViewDanhGia)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        txtTongSoBaiDang = view.findViewById(R.id.txtTongSoBaiDang)
        containerThongKeMon = view.findViewById(R.id.containerThongKeMon)
        searchView = view.findViewById(R.id.searchView)

        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = reviewList.filter {
                    it.noi_dung?.contains(s.toString(), ignoreCase = true) == true
                }
                reviewAdapter.updateList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        reviewAdapter = ReviewAdapter(requireContext(), reviewList) { review ->
            AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa đánh giá này không?")
                .setPositiveButton("Xóa") { _, _ -> xoaDanhGia(review) }
                .setNegativeButton("Hủy", null)
                .show()
        }

        recyclerView.adapter = reviewAdapter
        loadNguoiDungVaMonAn()
        docDanhSachDanhGia()
    }
    private fun loadNguoiDungVaMonAn() {
        val userRef = FirebaseDatabase.getInstance().getReference("nguoidung")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.key ?: continue
                    val ten = userSnapshot.child("ten").getValue(String::class.java) ?: "Ẩn danh"
                    tenNguoiDungMap[id] = ten
                }
                reviewAdapter.setTenNguoiDungMap(tenNguoiDungMap)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        val foodRef = FirebaseDatabase.getInstance().getReference("monan")
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val id = foodSnapshot.key ?: continue
                    val ten = foodSnapshot.child("tenmon").getValue(String::class.java) ?: "Không rõ"
                    tenMonAnMap[id] = ten
                }
                reviewAdapter.setTenMonAnMap(tenMonAnMap)
                // Sau khi có đủ dữ liệu tên, mới đọc danh sách đánh giá
                docDanhSachDanhGia()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun docDanhSachDanhGia() {
        val ref = FirebaseDatabase.getInstance().getReference("5/data")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reviewList.clear()
                for (child in snapshot.children) {
                    val review = child.getValue(ReviewModel::class.java)
                    review?.key = child.key // Lưu lại key cha
                    review?.let { reviewList.add(it) }
                }
                reviewAdapter.updateList(reviewList)
                txtTongSoBaiDang.text = "Tổng số dánh giá: ${reviewList.size}"
                hienThiThongKeTheoMon(reviewList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Lỗi đọc dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hienThiThongKeTheoMon(list: List<ReviewModel>) {
        val thongKe = list.groupingBy {
            val id = it.idma
            tenMonAnMap[id] ?: "Không rõ" }.eachCount()
        containerThongKeMon.removeAllViews()
        for ((mon, count) in thongKe) {
            val tv = TextView(requireContext())
            tv.text = "• $mon: $count bài"
            tv.setTextColor(Color.DKGRAY)
            containerThongKeMon.addView(tv)
        }
    }


    private fun xoaDanhGia(review: ReviewModel) {
        val key = review.key ?: return
        val ref = FirebaseDatabase.getInstance().getReference("5/data").child(key)
        ref.removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Đã xóa đánh giá", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show()
            }
    }
}
