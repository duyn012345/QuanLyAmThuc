package com.example.quanlyamthuc.model

data class UserModel(
    var userId: String? = null,
    val name: String = "",
    val email: String = "",
    val role: String = "user"
)
