package com.example.capstone_mursicapp;

public class ArtistModel {
    String artistName, artistImage, artistURI;
    int popularity;

    public ArtistModel (String artistName, String artistImage, String artistURI, int popularity){
        this.artistName = artistName;
        this.artistURI = artistURI;
        this.artistImage = artistImage;
        this.popularity = popularity;
    }


}
