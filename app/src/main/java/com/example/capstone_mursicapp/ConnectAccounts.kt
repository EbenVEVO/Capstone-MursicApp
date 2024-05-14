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
import com.example.capstone_mursicapp.data.remote.spotify.AuthenticationManager
import com.example.capstone_mursicapp.data.remote.spotify.SpotifyManager

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

        spotifylogin.text = "Checking Spotify Connection..."
        spotifylogin.isEnabled = false

        spotManager.isSpotifyConnected { isConnected ->
            if (isConnected) {
                spotifylogin.isEnabled = false
                spotifylogin.text = "Spotify Connected"
            } else {
                spotifylogin.isEnabled = true
                spotifylogin.text = "LOG IN SPOTIFY"
                spotifylogin.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(authManager.login()))
                    Log.i("MainActivity", "Opening Browser")
                    startActivity(browserIntent)
                }
            }
        }
        return view
    }
}