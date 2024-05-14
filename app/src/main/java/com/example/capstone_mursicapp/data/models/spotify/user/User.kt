package com.example.capstone_mursicapp.data.models.spotify.user

data class User(
    val display_name: String,
    val external_urls: ExternalUrls,
    val followers: Followers,
    val href: String,
    val id: String,
    val images: List<Any>,
    val type: String,
    val uri: String
)