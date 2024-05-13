package com.example.capstone_mursicapp.data.remote.spotify

import com.example.capstone_mursicapp.data.models.spotify.AccessToken
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SpotifyTokenApi {
    @FormUrlEncoded
    @POST("/api/token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("client_id") clientId: String,
        @Field("code_verifier") codeVerifier: String
    ): Response<AccessToken>

    @FormUrlEncoded
    @POST("/api/token")
    suspend fun refreshAccessToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String
    ): Response<AccessToken>
}