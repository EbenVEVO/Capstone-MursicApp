package com.example.capstone_mursicapp.data.remote.spotify

import android.util.Log
import com.example.capstone_mursicapp.data.SpotifyConstants.localAccessToken
import com.example.capstone_mursicapp.data.SpotifyConstants.CLIENT_ID
import com.example.capstone_mursicapp.data.SpotifyConstants.CODE_VERIFIER
import com.example.capstone_mursicapp.data.SpotifyConstants.REDIRECT_URI
import com.example.capstone_mursicapp.data.models.spotify.me.Me
import com.example.capstone_mursicapp.data.models.spotify.search.Search
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
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val documentReference = db.collection("Users").document(currentUser?.uid.toString())
    private var expiresIn: Long = 3600

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
                        localAccessToken = response.body()?.access_token.toString()
                        documentReference.update("accessToken", localAccessToken )
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
                                localAccessToken  = response.body()?.access_token.toString()
                                documentReference.update("accessToken", localAccessToken )
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

    fun getMe(callback: (Response<Me>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getMe(
                authorization = "Bearer $localAccessToken "
            )
            withContext(Dispatchers.Main) {
                callback(response)
            }
        }
    }

    fun getSearch(
        q: String? = null,
        type: Array<String>? = null,
        market: String? = null,
        limit: Int = 20,
        offset: Int? = null,
        includeExternal: String? = null,
        callback: (Response<Search>?) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = spotifyApi.getSearch(
                authorization = "Bearer ${localAccessToken}", q, type, market, limit, offset, includeExternal
            )
            withContext(Dispatchers.Main) {
                callback(response)
            }
        }
    }


}
///"BQClAO2tUdsLlPcqSqOX2sG7WBebI2OT32QckWEfov7wbHE6ShmTofw7ITC5PuuiaNHSsD0_OOx2OXGqc6xNPw_zkx2d9UBN3mKnN5Ig9ZxyBCBDHMpSqfWgJvk_CWMd3OeZmLQSPAOLivGqg7YyWzv1d1W7UVPipsLEoOLVTlj8Wrg4TCctkRSVYj65rDsS-AUNLHRaWWjrZDbzunBzd64Ln3Bl6JZoOUJbGz5IAGeUbxRsnAY8WkOkUERl7NhYkTT2032-Y0zqbLQ88xypEaiTo4t8787IAUCNs5WZ5HkQOcCv1aIQpw"/
//expired access token

//documentReference.get()
//    .addOnSuccessListener { documentSnapshot ->
//        if (documentSnapshot.exists()) {
//            callback(documentSnapshot.getBoolean("isSpotifyConnected") ?: false)
//        } else {
//            callback(false)
//        }
//    }

//New Funciton Template
//fun get(callback: (Response<Me>?) -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val response = spotifyApi.get(
//            authorization = "Bearer $localAccessToken "
//        )
//        withContext(Dispatchers.Main) {
//            callback(response)
//        }
//    }
//}
//
//Function Call Template
//spotManager.get(
//    //params
//) { response ->
//    if (response != null) {
//        Log.i("getSearch", response.body().toString())
//    } else {
//        Log.e("getSearch", "error with call")
//    }
//}