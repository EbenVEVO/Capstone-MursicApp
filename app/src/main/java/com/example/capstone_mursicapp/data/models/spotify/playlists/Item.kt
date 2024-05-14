package com.example.capstone_mursicapp.data.models.spotify.playlists

data class Item(
    val collaborative: Boolean,
    val description: String,
    val external_urls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val owner: Owner,
    val primary_color: String,
    val `public`: Boolean,
    val snapshot_id: String,
    val tracks: Tracks,
    val type: String,
    val uri: String
)