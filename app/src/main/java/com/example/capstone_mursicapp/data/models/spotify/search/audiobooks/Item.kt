package com.example.capstone_mursicapp.data.models.spotify.search.audiobooks

data class Item(
    val authors: List<Author>,
    val available_markets: List<String>,
    val copyrights: List<Any>,
    val description: String,
    val edition: String,
    val explicit: Boolean,
    val external_urls: ExternalUrls,
    val href: String,
    val html_description: String,
    val id: String,
    val images: List<Image>,
    val is_externally_hosted: Any,
    val languages: List<String>,
    val media_type: String,
    val name: String,
    val narrators: List<Narrator>,
    val publisher: String,
    val total_chapters: Int,
    val type: String,
    val uri: String
)