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
import com.example.capstone_mursicapp.data.SpotifyConstants
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager

class ArtistSearch : AppCompatActivity() {
    var resultslist: MutableList<ArtistModel> = mutableListOf()
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
                       Log.i("test", response.toString())
                      /*  for (i in 0..8) {
                            var artistName:String
                            var artistImage:String
                            var artistURI:String
                            var popularity: Int

                            artistName = response.body()!!.artists.items[i].name.toString()
                            artistImage = response.body()!!.artists.items[i].images[0].url.toString()
                            artistURI = response.body()!!.artists.items[i].uri.toString()
                            popularity = response.body()!!.artists.items[i].popularity

                            val artistModel = ArtistModel(artistName, artistImage, artistURI, popularity)

                            resultslist.add(artistModel)


                        }*/
                    } else {
                        Log.e("artist", "error with call")
                    }
                }
                resultslist.forEach { artistModel ->
                    Log.i("results", artistModel.artistImage+" "+ artistModel.artistName+ " "+  artistModel.artistURI + " "+ artistModel.popularity )
                }
            return false
            }
        })
    }
}
