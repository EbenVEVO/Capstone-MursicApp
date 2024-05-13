package com.example.capstone_mursicapp.data.models.spotify

data class AccessToken(
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val scope: String,
    val token_type: String
)