package com.example.capstone_mursicapp.data.models.spotify.top

data class Top(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: String,
    val total: Int
)