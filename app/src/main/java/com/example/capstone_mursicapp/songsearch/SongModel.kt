package com.example.capstone_mursicapp.songsearch

class SongModel {
    constructor(
        albumImage: String,
        songName: String,
        artistName: String, //change to array of string due to multiple artists can be same song
        isExplicit: Boolean,
        previewUrl: String?,
        songId: String
    ) {
        this.albumImage = albumImage
        this.songName = songName
        this.artistName = artistName
        this.isExplicit = isExplicit
        this.previewUrl = previewUrl
        this.songId = songId
    }

    var albumImage : String
    var songName : String
    var artistName : String
    var isExplicit : Boolean
    var previewUrl : String?
    var songId : String
}