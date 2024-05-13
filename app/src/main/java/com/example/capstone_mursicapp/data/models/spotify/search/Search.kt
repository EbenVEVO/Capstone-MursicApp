package com.example.capstone_mursicapp.data.models.spotify.search

import com.example.capstone_mursicapp.data.models.spotify.search.albums.Albums
import com.example.capstone_mursicapp.data.models.spotify.search.artist.Artists
import com.example.capstone_mursicapp.data.models.spotify.search.audiobooks.Audiobooks
import com.example.capstone_mursicapp.data.models.spotify.search.episodes.Episodes
import com.example.capstone_mursicapp.data.models.spotify.search.playlists.Playlists
import com.example.capstone_mursicapp.data.models.spotify.search.shows.Shows
import com.example.capstone_mursicapp.data.models.spotify.search.tracks.Tracks

data class Search(
    val albums: Albums,
    val artists: Artists,
    val audiobooks: Audiobooks,
    val episodes: Episodes,
    val playlists: Playlists,
    val shows: Shows,
    val tracks: Tracks
)