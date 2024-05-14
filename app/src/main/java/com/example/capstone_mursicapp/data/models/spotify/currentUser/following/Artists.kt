package com.example.capstone_mursicapp.data.models.spotify.currentUser.following

data class Artists(
    val cursors: Cursors,
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val total: Int
)