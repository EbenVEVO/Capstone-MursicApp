package com.example.capstone_mursicapp.data.models.spotify.search.playlists

data class Playlists(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)