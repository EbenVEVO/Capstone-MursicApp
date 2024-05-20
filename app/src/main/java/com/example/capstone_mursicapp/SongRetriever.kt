package com.example.capstone_mursicapp

import android.util.Log
import com.example.capstone_mursicapp.data.models.spotify.tracks.album.Artist
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager

class SongRetriever {

    var spotManager: SpotifyManager = SpotifyManager()

    fun getSongImage(songId:String):String {
        var songImage = ""
        if (songId != null) {
            spotManager.getTrack(songId) { response ->
                Log.e("E", response!!.toString())
                if (response != null) {
                    songImage = response.body()!!.album.images[0].url
                }
            }
        }
        return songImage
    }
    fun getArtistName(songId: String): List<String>{
        var artistName = mutableListOf<String>()
            if (songId != null) {
            spotManager.getTrack(songId) { response ->
                Log.e("E", response!!.toString())
                if (response != null) {
                   response.body()!!.album.artists.forEach{artist: Artist ->
                       artistName.add(artist.name)}
                }
            }
        }
        return artistName
    }
    fun getSongName(songId:String): String{
        var songName = ""
        if (songId != null) {
            spotManager.getTrack(songId) { response ->
                Log.e("E", response!!.toString())
                if (response != null) {
                    songName = response.body()!!.album.name
                }
            }
        }
        return songName
    }
}