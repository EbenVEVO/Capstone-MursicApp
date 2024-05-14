package com.example.capstone_mursicapp.data

import android.util.Log

object SpotifyConstants {
    private const val CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.-~"
    const val CLIENT_ID = "61359d946eb04a4ebbf564d319ed1f26"
    const val REDIRECT_URI = "apibasictest://callback"
    const val SCOPE = "playlist-read-private playlist-read-collaborative playlist-modify-private playlist-modify-public user-follow-read user-read-recently-played user-top-read user-library-read user-read-email user-read-private"
    val CODE_VERIFIER : String by lazy { generateRandomString(128) }
    val STATE: String by lazy { generateRandomString(32) }
    lateinit var localAccessToken : String

    private fun generateRandomString(len: Int): String {
        Log.i("Random", "generated random string")
        return (1..len)
            .map { CHARSET.random() }
            .joinToString("")
    }
}