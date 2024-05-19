package com.example.capstone_mursicapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager
import com.example.capstone_mursicapp.functionality.MediaPlayerManager
import com.example.capstone_mursicapp.songsearch.SongModel
import com.example.capstone_mursicapp.songsearch.SongSearchAdapter

class SongActivity : AppCompatActivity() {
    var songModels = mutableListOf<SongModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_song)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val spotManager = SpotifyManager()
        val searchBar: SearchView = findViewById(R.id.searchBar)
        val searchResults: RecyclerView = findViewById(R.id.searchResults)

        searchResults.layoutManager = LinearLayoutManager(this)
        val adapter = SongSearchAdapter(songModels) { position -> onItemClicked(position) }
        searchResults.adapter = adapter


        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("onQueryTextSubmit", "Submit")
                // Handle the case when the user submits the search query
                // You can perform a search operation or any other desired action here
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.i("onQueryTextChange", "Change")
                MediaPlayerManager.stopAudio()
                if (query!!.isNotEmpty()) {
                    spotManager.getSearch(
                        q = query,
                        type = arrayOf("track"),
                        //limit = 3
                    ) { response ->
                        if (response?.body() != null) {
                            songModels.clear()
                            adapter.notifyDataSetChanged()
                            for (item in response.body()!!.tracks.items) {
                                songModels.add(
                                    SongModel(
                                        item.album.images[1].url,
                                        item.name,
                                        item.artists[0].name,
                                        item.explicit,
                                        item.preview_url,
                                        item.id
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged() //unsure
                        }
                    }
                }
                return true
            }
        })
    }

    private fun onItemClicked(position: Int) {
        val resultIntent = Intent()
        resultIntent.putExtra("songId", songModels[position].songId)
        Log.i("songId", position.toString())
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("DESTROY", "DESTROY")
        MediaPlayerManager.releaseMediaPlayer()
    }

}