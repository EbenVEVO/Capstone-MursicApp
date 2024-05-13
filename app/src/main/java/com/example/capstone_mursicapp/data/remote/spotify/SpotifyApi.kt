package com.example.capstone_mursicapp.data.remote.spotify

import com.example.capstone_mursicapp.data.models.spotify.me.Me
import com.example.capstone_mursicapp.data.models.spotify.search.Search
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApi {
    @GET("/v1/me")
    suspend fun getMe(
        @Header("Authorization") authorization: String
    ): Response<Me>

    @GET("/v1/search")
    suspend fun getSearch(
        @Header("Authorization") authorization: String,
        @Query("q") q : String?,
        @Query("type") type : Array<String>?,
        @Query("market") market : String?,
        @Query("limit") limit : Int,
        @Query("offset") offset : Int?,
        @Query("include_external") includeExternal : String?
    ): Response<Search>

}