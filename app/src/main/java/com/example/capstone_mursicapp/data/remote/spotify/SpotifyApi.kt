package com.example.apibasictest.data.remote.spotify

import com.example.apibasictest.data.models.spotify.me.Me
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface SpotifyApi {
    @GET("/v1/me")
    suspend fun getMe(@Header("Authorization") authorization: String): Response<Me>
}