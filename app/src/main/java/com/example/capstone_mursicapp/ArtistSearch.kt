package com.example.capstone_mursicapp

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager

class ArtistSearch : AppCompatActivity() {
    var resultslist: MutableList<ArtistModel> = mutableListOf()
    var spotifyManager: SpotifyManager = SpotifyManager()
    var searchView: SearchView? = null
    var artistSearchAdapter: ArtistSearchAdapter? = null
    var artistresults: RecyclerView? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_artist_search)

        searchView = findViewById<SearchView>(R.id.search)
        artistresults = findViewById<RecyclerView>(R.id.artistresults)

        artistSearchAdapter = ArtistSearchAdapter(resultslist)
        artistresults?.let {
            it.adapter = artistSearchAdapter
        }

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                resultslist.clear()
                Log.i("artist", query)
                if( query.length > 1) {
                    spotifyManager.getSearch(
                        q = query,
                        type = arrayOf("artist")
                    ) { response ->
                        Log.i("artist", response.toString())

                        if (response != null && response.body() != null) {
                            for (item in response.body()!!.artists.items ) {
                                var artistName: String
                                var artistImage: String
                                var artistURI: String
                                var popularity: Int

                                artistName = item.name.toString()
                                artistImage = item.images[0].url.toString()
                                artistURI = item.uri.toString()
                                popularity = item.popularity

                                var artistModel =
                                    ArtistModel(artistName, artistImage, artistURI, popularity)

                                resultslist.add(artistModel)
                                artistSearchAdapter!!.setArtist(resultslist)
                                artistSearchAdapter!!.notifyDataSetChanged()

                            }
                        } else {
                            Log.e("artist", "error with call")
                        }
                    }
                }
            return false
            }
        })
        if (artistSearchAdapter != null) {
            artistSearchAdapter!!.setArtist(resultslist)
            artistSearchAdapter!!.notifyDataSetChanged()
        }
    }


}
