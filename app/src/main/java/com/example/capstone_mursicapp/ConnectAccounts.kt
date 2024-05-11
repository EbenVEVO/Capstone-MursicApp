package com.example.capstone_mursicapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.apibasictest.data.remote.spotify.AuthenticationManager
import com.example.apibasictest.data.remote.spotify.SpotifyManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.firestore.FirebaseFirestore

class ConnectAccounts : Fragment() {
    private val authManager = AuthenticationManager()
    private val spotManager = SpotifyManager()
    private lateinit var spotifylogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connect_accounts, container, false)
        spotifylogin = view.findViewById(R.id.spotifylogin)

        spotManager.isSpotifyConnected { isConnected ->
            spotifylogin.isEnabled = !isConnected
            spotifylogin.hint = if (isConnected) "Spotify Connected" else "Connect"
        }
        spotifylogin.setOnClickListener {
            Log.i("MainActivity", "Button Click")
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authManager.login()))
            Log.i("MainActivity", "Opening Browser")
            startActivity(browserIntent)
        }
        return view
    }
}