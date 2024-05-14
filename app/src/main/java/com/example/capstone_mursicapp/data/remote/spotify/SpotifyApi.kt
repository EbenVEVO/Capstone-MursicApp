package com.example.capstone_mursicapp.data.remote.spotify

import com.example.capstone_mursicapp.data.models.spotify.currentUser.following.Following
import com.example.capstone_mursicapp.data.models.spotify.currentUser.me.CurrentUser
import com.example.capstone_mursicapp.data.models.spotify.playlists.Playlists
import com.example.capstone_mursicapp.data.models.spotify.search.Search
import com.example.capstone_mursicapp.data.models.spotify.top.Top
import com.example.capstone_mursicapp.data.models.spotify.tracks.Tracks
import com.example.capstone_mursicapp.data.models.spotify.tracks.track.Track
import com.example.capstone_mursicapp.data.models.spotify.user.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApi {
    @GET("/v1/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<CurrentUser>

    @GET("/v1/me/top/{type}")
    suspend fun getCurrentUserTop(
        @Header("Authorization") authorization: String,
        @Path("type") type: String,
        @Query("time_range") timeRange: String?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Response<Top>

    @GET("/v1/me/playlists")
    suspend fun getCurrentUserPlaylists(
        @Header("Authorization") authorization: String
    ): Response<Playlists>

    @GET("/v1/me/following")
    suspend fun getCurrentUserFollowing(
        @Header("Authorization") authorization: String,
        @Query("after") after : String?,
        @Query("limit") limit : Int?,
        @Query("type") type : String = "artist"
    ): Response<Following>

    @GET("/v1/users/{user_id}")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: String
    ): Response<User>

    @GET("/v1/tracks")
    suspend fun getTracks(
        @Header("Authorization") authorization: String,
        @Query("ids") ids : String,
        @Query("market") market : String?
    ): Response<Tracks>

    @GET("/v1/tracks/{id}")
    suspend fun getTrack(
        @Header("Authorization") authorization: String,
        @Path("id") id : String,
        @Query("market") market : String?
    ): Response<Track>


    @GET("/v1/search")
    suspend fun getSearch(
        @Header("Authorization") authorization: String,
        @Query("q") q : String?,
        @Query("type") type : Array<String>?,
        @Query("market") market : String?,
        @Query("limit") limit : Int?,
        @Query("offset") offset : Int?,
        @Query("include_external") includeExternal : String?
    ): Response<Search>



    //https://developer.spotify.com/documentation/web-api/reference/get-users-saved-tracks
    //Next
}