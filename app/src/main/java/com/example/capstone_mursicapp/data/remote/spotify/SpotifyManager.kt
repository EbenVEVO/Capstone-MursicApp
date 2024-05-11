package com.example.apibasictest.data.remote.spotify

import android.util.Log
import com.example.apibasictest.data.SpotifyConstants.CLIENT_ID
import com.example.apibasictest.data.SpotifyConstants.CODE_VERIFIER
import com.example.apibasictest.data.SpotifyConstants.REDIRECT_URI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


class SpotifyManager {


    private val spotifyApi: SpotifyApi = SpotifyApiBuilder.getSpotifyApi()
    private val spotifytokenApi: SpotifyTokenApi = SpotifyTokenBuilder.getSpotifyTokenApi()

    //firebase
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val documentReference = db.collection("Users").document(currentUser?.uid.toString())
    private lateinit var localAccessToken: String
    private var expiresIn: Long = 3600

    //cleanup
    fun getAccessToken(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = spotifytokenApi.getAccessToken(
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

                        documentReference.update(
                            "expiresAt",
                            System.currentTimeMillis() + (expiresIn * 1000)
                        )
                        documentReference.update("isSpotifyConnected", true)
                    }
                } else {
                    Log.e(
                        "getAccessToken",
                        "Failed to get Access Token: ${response.errorBody()?.string()}"
                    )
                    Log.e("getAccessToken", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("getAccessToken", "Error: ${e.message}", e)
            }
        }
    }

    init { // Schedule a recurring task to refresh the token every 3600 -> (expiresIn) seconds (1 hour)
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                refreshAccessToken()
            }
        }, 0, expiresIn * 1000)
    }

    fun refreshAccessToken() {
        Log.i("refreshAccessToken", "Refreshing")
        //cleanup
        documentReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val refreshToken = document.getString("refreshToken")
                    if (refreshToken != null) {
                        //-------------
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = spotifytokenApi.refreshAccessToken(
                                    refreshToken = refreshToken,
                                    clientId = CLIENT_ID,
                                )
                                if (response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        expiresIn = response.body()!!.expires_in.toLong()
                                        documentReference.update(
                                            "accessToken",
                                            response.body()?.access_token
                                        )
                                        localAccessToken = response.body()?.access_token.toString()
                                        documentReference.update(
                                            "refreshToken",
                                            response.body()?.refresh_token
                                        )
                                        documentReference.update(
                                            "expiresAt",
                                            System.currentTimeMillis() + (response.body()!!.expires_in * 1000)
                                        )
                                    }
                                } else {
                                    Log.e(
                                        "refreshAccessToken",
                                        "Failed to refresh Access Token: ${
                                            response.errorBody()?.string()
                                        }"
                                    )
                                    Log.e("refreshAccessToken", "Response code: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("refreshAccessToken", "Error: ${e.message}", e)
                            }
                        }
                        //-----------------
                    } else {
                        Log.e("refreshAccessToken", "No refreshToken found")
                    }
                } else {
                    Log.e("refreshAccessToken", "No Value")
                }
            }
    }

    fun isSpotifyConnected(callback: (Boolean) -> Unit) {
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    callback(documentSnapshot.getBoolean("isSpotifyConnected") ?: false)
                } else {
                    callback(false)
                }
            }
    }

    fun getMe() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = spotifyApi.getMe(
                    authorization = "Bearer $localAccessToken"
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Log.i("Image", response.body()!!.images[0].height.toString())
                    }
                } else {
                    Log.e("getMe", "Failed: ${response.errorBody()?.string()}")
                    Log.e("getMe", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("getMe", "Error: ${e.message}", e)
            }
        }
    }
}


//CoroutineScope(Dispatchers.IO).launch {
//    try {
//        val response = spotifyApi.___()
//        if (response.isSuccessful) {
//            withContext(Dispatchers.Main) {
//                Log.i("Data", response.body().toString())
//            }
//        } else {
//            Log.e("funName", "Failed: ${response.errorBody()?.string()}")
//            Log.e("funName", "Response code: ${response.code()}")
//        }
//    } catch (e: Exception) {
//        Log.e("funName", "Error: ${e.message}", e)
//    }
//}
///"BQClAO2tUdsLlPcqSqOX2sG7WBebI2OT32QckWEfov7wbHE6ShmTofw7ITC5PuuiaNHSsD0_OOx2OXGqc6xNPw_zkx2d9UBN3mKnN5Ig9ZxyBCBDHMpSqfWgJvk_CWMd3OeZmLQSPAOLivGqg7YyWzv1d1W7UVPipsLEoOLVTlj8Wrg4TCctkRSVYj65rDsS-AUNLHRaWWjrZDbzunBzd64Ln3Bl6JZoOUJbGz5IAGeUbxRsnAY8WkOkUERl7NhYkTT2032-Y0zqbLQ88xypEaiTo4t8787IAUCNs5WZ5HkQOcCv1aIQpw"/
//expired access token