package com.example.capstone_mursicapp.data.remote.spotify

import android.util.Log
import com.example.capstone_mursicapp.data.SpotifyConstants.localAccessToken
import com.example.capstone_mursicapp.data.SpotifyConstants.CLIENT_ID
import com.example.capstone_mursicapp.data.SpotifyConstants.CODE_VERIFIER
import com.example.capstone_mursicapp.data.SpotifyConstants.REDIRECT_URI
import com.example.capstone_mursicapp.data.SpotifyConstants.expiresIn
import com.example.capstone_mursicapp.data.models.spotify.currentUser.following.Following
import com.example.capstone_mursicapp.data.models.spotify.currentUser.me.CurrentUser
import com.example.capstone_mursicapp.data.models.spotify.playlists.Playlists
import com.example.capstone_mursicapp.data.models.spotify.search.Search
import com.example.capstone_mursicapp.data.models.spotify.top.Top
import com.example.capstone_mursicapp.data.models.spotify.tracks.Tracks
import com.example.capstone_mursicapp.data.models.spotify.tracks.track.Track
import com.example.capstone_mursicapp.data.models.spotify.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Response

class SpotifyManager {
    //api interfaces
    private val spotifyApi: SpotifyApi = SpotifyApiBuilder.getSpotifyApi()
    private val spotifyTokenApi: SpotifyTokenApi = SpotifyTokenBuilder.getSpotifyTokenApi()

    //firebase
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var documentReference = db.collection("Users").document(currentUser?.uid.toString())

    fun getAccessToken(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = spotifyTokenApi.getAccessToken(
                    code = code,
                    redirectUri = REDIRECT_URI,
                    clientId = CLIENT_ID,
                    codeVerifier = CODE_VERIFIER
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        expiresIn = response.body()!!.expires_in.toLong()
                        documentReference.update("accessToken", response.body()?.access_token)
                        localAccessToken = response.body()?.access_token.toString()
                        documentReference.update("refreshToken", response.body()?.refresh_token)
                        documentReference.update("expiresAt", System.currentTimeMillis() + (expiresIn * 1000))
                        documentReference.update("isSpotifyConnected", true)
                    }
                } else {
                    Log.e("getAccessToken", "Failed to get Access Token: ${response.errorBody()?.string()}")
                    Log.e("getAccessToken", "Response code: ${response.code()}")
                }
            } catch (e: Exception) { Log.e("getAccessToken", "Error: ${e.message}", e) }
        }
    }

    fun refreshAccessToken() {
        Log.i("refreshAccessToken", "Refreshing")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = documentReference.get().await()
                if (document.exists()) {
                    val refreshToken = document.getString("refreshToken")
                    if (refreshToken != null) {
                        val response = try {
                            spotifyTokenApi.refreshAccessToken(
                                refreshToken = refreshToken,
                                clientId = CLIENT_ID,
                            )
                        } catch (e: Exception) {
                            Log.e("refreshAccessToken", "Error refreshing token: ${e.message}", e)
                            documentReference.update("isSpotifyConnected", false)
                            null }
                        if (response?.isSuccessful == true) {
                            withContext(Dispatchers.Main) {
                                expiresIn = response.body()!!.expires_in.toLong()
                                localAccessToken = response.body()?.access_token.toString()
                                documentReference.update("accessToken", localAccessToken)
                                documentReference.update("refreshToken", response.body()?.refresh_token)
                                documentReference.update("expiresAt", System.currentTimeMillis() + (response.body()!!.expires_in * 1000))
                            }
                        } else {
                            Log.e("refreshAccessToken", "Failed to refresh Access Token: ${response?.errorBody()?.string()}")
                            Log.e("refreshAccessToken", "Response code: ${response?.code()}")
                            documentReference.update("isSpotifyConnected", false)
                        }
                    } else { Log.e("refreshAccessToken", "No refreshToken found") }
                } else { Log.e("refreshAccessToken", "No Value") }
            } catch (e: Exception) {
                Log.e("refreshAccessToken", "Error: ${e.message}", e)
            }
        }
    }

    fun isSpotifyConnected(callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val documentSnapshot = documentReference.get().await()
                withContext(Dispatchers.Main) {
                    callback(documentSnapshot.exists() && documentSnapshot.getBoolean("isSpotifyConnected") ?: false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-current-users-profile
    fun getCurrentUser(callback: (Response<CurrentUser>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getCurrentUser(authorization = "Bearer $localAccessToken")
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-users-top-artists-and-tracks
    fun getCurrentUserTop(
        type: String, //Allowed values: "artists", "tracks"
        timeRange: String? = null, //long_term (1yr), medium_term (6 months), short_term (1 month). Default: medium_term
        limit: Int? = null,
        offset: Int? = null,
        callback: (Response<Top>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getCurrentUserTop(authorization = "Bearer $localAccessToken", type, timeRange, limit, offset)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-a-list-of-current-users-playlists
    fun getCurrentUserPlaylists(callback: (Response<Playlists>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getCurrentUserPlaylists(authorization = "Bearer $localAccessToken")
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-followed
    fun getCurrentUserFollowing(
        after: String? = null,
        limit: Int? = null,
        callback: (Response<Following>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getCurrentUserFollowing(authorization = "Bearer $localAccessToken", after, limit)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-users-profile
    fun getUser(
        userId : String, //The unique string identifying the Spotify user that you can find at the end of the Spotify URI for the user.
        callback: (Response<User>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getUser(authorization = "Bearer $localAccessToken", userId)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-several-tracks
    fun getTracks(
        market: String? = null,
        ids: String, //Comma separated string of id's, no spaces allowed : "ID1,ID2,ID3..."
        callback: (Response<Tracks>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getTracks(authorization = "Bearer $localAccessToken", ids, market)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/get-track
    fun getTrack(
        id: String,
        market: String? = null,
        callback: (Response<Track>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getTrack(authorization = "Bearer $localAccessToken", id, market)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

    //https://developer.spotify.com/documentation/web-api/reference/search
    fun getSearch(
        q: String? = null,
        type: Array<String>? = null, //Allowed values: "album", "artist", "playlist", "track", "show", "episode", "audiobook"
        market: String? = null,
        limit: Int? = null,
        offset: Int? = null,
        includeExternal: String? = null,
        callback: (Response<Search>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getSearch(authorization = "Bearer $localAccessToken", q, type, market, limit, offset, includeExternal)
            withContext(Dispatchers.Main) { callback(response) }
        }
    }

}
///"BQClAO2tUdsLlPcqSqOX2sG7WBebI2OT32QckWEfov7wbHE6ShmTofw7ITC5PuuiaNHSsD0_OOx2OXGqc6xNPw_zkx2d9UBN3mKnN5Ig9ZxyBCBDHMpSqfWgJvk_CWMd3OeZmLQSPAOLivGqg7YyWzv1d1W7UVPipsLEoOLVTlj8Wrg4TCctkRSVYj65rDsS-AUNLHRaWWjrZDbzunBzd64Ln3Bl6JZoOUJbGz5IAGeUbxRsnAY8WkOkUERl7NhYkTT2032-Y0zqbLQ88xypEaiTo4t8787IAUCNs5WZ5HkQOcCv1aIQpw"/
//expired access token

//
//
//