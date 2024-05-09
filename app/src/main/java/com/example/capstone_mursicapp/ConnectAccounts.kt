package com.example.capstone_mursicapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ConnectAccounts : Fragment() {
    var spotifylogin: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_connect_accounts, container, false)
        spotifylogin = view.findViewById(R.id.spotifylogin)
        spotifylogin.setOnClickListener(View.OnClickListener { })
        return view
    }
}