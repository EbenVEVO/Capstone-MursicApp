package com.example.capstone_mursicapp.data.remote.spotify

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.capstone_mursicapp.data.SpotifyConstants.CLIENT_ID
import com.example.capstone_mursicapp.data.SpotifyConstants.CODE_VERIFIER
import com.example.capstone_mursicapp.data.SpotifyConstants.REDIRECT_URI
import com.example.capstone_mursicapp.data.SpotifyConstants.SCOPE
import com.example.capstone_mursicapp.data.SpotifyConstants.STATE
import java.security.MessageDigest
import java.util.Base64

class AuthenticationManager {
    private val spotManager = SpotifyManager()
    @RequiresApi(Build.VERSION_CODES.O)
    fun codeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.toByteArray())
        return Base64.getUrlEncoder().encodeToString(digest).replace("=", "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun login(): String {
        //Log.i("entry", state)
        val challenge = codeChallenge(CODE_VERIFIER)
        Log.i("CHALLENGE", challenge)
        val builder: Uri.Builder = Uri.Builder()
        builder.scheme("https")
            .authority("accounts.spotify.com")
            .appendPath("authorize")

        builder.appendQueryParameter("client_id", CLIENT_ID)
        builder.appendQueryParameter("response_type", "code")
        builder.appendQueryParameter("redirect_uri", REDIRECT_URI)
        builder.appendQueryParameter("state", STATE)
        builder.appendQueryParameter("scope", SCOPE)
        builder.appendQueryParameter("code_challenge_method", "S256")
        builder.appendQueryParameter("code_challenge", challenge)
        //builder.appendQueryParameter("show_dialog", "True") //optional to ask every time to log in
        return (builder.build().toString())
    }



    fun handleSpotCallback(uri: Uri?) {
        Log.i("handleSpotCallback", "WERE IN")
        //fix later
        if (uri != null && "apibasictest" == uri.scheme && "callback" == uri.host) {
            //handle callback on uri to get code
            handleCallback(uri)?.let {
                spotManager.getAccessToken(it)
            }
        }
    }

    fun handleCallback(uri: Uri): String? {


        //if no error and states match, return code if not null
        if (uri.getQueryParameter("error") != null) {
            Log.e("CallbackHandler", "Error on callback ${uri.getQueryParameter("error")}")
            return null
        }
        if (STATE != uri.getQueryParameters("state")[0]) {
            Log.e(
                "CallbackHandler",
                "Error on callback: Mismatched State $STATE ${uri.getQueryParameters("state")[0]}"
            )
            return null
        }
        return uri.getQueryParameter("code") ?: run {
            Log.e(
                "AuthenticationManager",
                "Invalid callback URI: $uri"
            ); null
        }
    }

}