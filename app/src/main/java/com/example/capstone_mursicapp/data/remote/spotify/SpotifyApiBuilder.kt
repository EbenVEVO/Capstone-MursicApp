package com.example.capstone_mursicapp.data.remote.spotify

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SpotifyApiBuilder {
    private val retrofit: Retrofit by lazy { //check private
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSpotifyApi(): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }
}

object SpotifyTokenBuilder {
    private val retrofit: Retrofit by lazy { //check private
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getSpotifyTokenApi(): SpotifyTokenApi {
        return retrofit.create(SpotifyTokenApi::class.java)
    }
}