package com.example.capstone_mursicapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager

class ArtistSearch : AppCompatActivity() {
    var spotifyManager: SpotifyManager = SpotifyManager()
    var searchView: SearchView? = null
    var artistresults: RecyclerView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_artist_search)

        searchView = findViewById<SearchView>(R.id.search)
        artistresults = findViewById<RecyclerView>(R.id.artistresults)
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.i("artist", query)
                spotifyManager.getSearch(
                        q = query,
                        type = arrayOf("artist")
                ) { response ->
                    if (response != null) {
                        Log.i("artist", response.body().toString())
                    } else {
                        Log.e("artist", "error with call")
                    }

            }
            return false
            }
        })
    }
}
