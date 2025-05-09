package com.example.quanlyamthuc.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.quanlyamthuc.R
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlyamthuc.databinding.FragmentDishBinding
import com.example.quanlyamthuc.model.DishModel
import com.example.quanlyamthuc.adapter.DishAdapter
import com.google.firebase.database.*

    class DishFragment : Fragment() {

        private lateinit var binding: FragmentDishBinding
        private lateinit var monanRef: DatabaseReference
        private lateinit var dishAdapter: DishAdapter
        private val monAnList = mutableListOf<DishModel>()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentDishBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.dishRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.dishRecyclerView.setHasFixedSize(true)
            // Tải danh sách tỉnh thành
            getDishData()

            monanRef = FirebaseDatabase.getInstance().getReference("10/data")

            dishAdapter = DishAdapter(
                monAnList,
                requireContext(),
                onEditClick = { showDishDialog(it) },
                onDeleteClick = { confirmDelete(it) }
            )

            binding.dishRecyclerView.adapter = dishAdapter

            binding.fabAddDish.setOnClickListener {
                showDishDialog(null)
            }
//
//            binding.dishRecyclerView.apply {
//                layoutManager = LinearLayoutManager(context)
//                adapter = dishAdapter
//            }
        }
        private fun getDishData() {
            monanRef = FirebaseDatabase.getInstance().getReference("10/data")
            monanRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    monAnList.clear()
                    for (monSnapshot in snapshot.children) {
                        val monAn = monSnapshot.getValue(DishModel::class.java)
                        monAn?.let { monAnList.add(it) }
                    }
                    dishAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        private fun showDishDialog(dishToEdit: DishModel?) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_dish, null)
            val edtImg1 = dialogView.findViewById<EditText>(R.id.dishImage)
            val edtImg2 = dialogView.findViewById<EditText>(R.id.dishImage2)
            val edtImg3 = dialogView.findViewById<EditText>(R.id.dishImage3)
            val edtTenma = dialogView.findViewById<EditText>(R.id.edtTenMon)
            val edtGioithieu = dialogView.findViewById<EditText>(R.id.edtGioiThieu)
            val edtGiaca = dialogView.findViewById<EditText>(R.id.edtGia)
            val edtMota = dialogView.findViewById<EditText>(R.id.edtMoTa)
            val edtDiachi = dialogView.findViewById<EditText>(R.id.edtDiaChi)
            val edtLink = dialogView.findViewById<EditText>(R.id.edtLink)

            val isEdit = dishToEdit != null

            dishToEdit?.let {
                edtImg1.setText(it.hinhanh)
                edtImg2.setText(it.hinhanh2)
                edtImg3.setText(it.hinhanh3)
                edtTenma.setText(it.tenma)
                edtDiachi.setText(it.diachi)
                edtGioithieu.setText(it.gioithieu)
                edtGiaca.setText(it.giaca)
                edtMota.setText(it.mota)
                edtLink.setText(it.duonglink_diachi)
            }

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(if (isEdit) "Sửa món ăn" else "Thêm món ăn")
                .setView(dialogView)
                .setPositiveButton("Lưu", null)
                .setNegativeButton("Hủy", null)
                .create()

            dialog.setOnShowListener {
                val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                btnSave.setOnClickListener {
                    val img1 = edtImg1.text.toString().trim()
                    val img2 = edtImg2.text.toString().trim()
                    val img3 = edtImg3.text.toString().trim()
                    val ten = edtTenma.text.toString().trim()
                    val dc = edtDiachi.text.toString().trim()
                    val gt = edtGioithieu.text.toString().trim()
                    val gia = edtGiaca.text.toString().trim()
                    val mt = edtMota.text.toString().trim()
                    val linkDC = edtLink.text.toString().trim()

                    if (img1.isEmpty() || img2.isEmpty() || img3.isEmpty() || ten.isEmpty() || dc.isEmpty() || gt.isEmpty() || gia.isEmpty() || mt.isEmpty() || linkDC.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val dishId = dishToEdit?.idma ?: monanRef.push().key!!
                    val dish = DishModel().apply {
                        hinhanh = img1
                        hinhanh2 = img2
                        hinhanh3 = img3
                        idma = dishId
                        tenma = ten
                        diachi = dc
                        gioithieu = gt
                        giaca = gia
                        mota = mt
                        duonglink_diachi = linkDC
//                        hinhanh = dishToEdit?.hinhanh
//                        hinhanh2 = dishToEdit?.hinhanh2
//                        hinhanh3 = dishToEdit?.hinhanh3
                    }

                    monanRef.child(dishId).setValue(dish)
                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        private fun confirmDelete(dish: DishModel) {
            AlertDialog.Builder(requireContext())
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc muốn xóa món '${dish.tenma}' không?")
                .setPositiveButton("Xóa") { _, _ ->
                    dish.idma?.let {
                        monanRef.child(it).removeValue()
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }





