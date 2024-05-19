package com.example.capstone_mursicapp.songsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone_mursicapp.ImageLoader
import com.example.capstone_mursicapp.R
import com.example.capstone_mursicapp.functionality.MediaPlayerManager


class SongSearchAdapter(
    private var songModels: MutableList<SongModel>,
    private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<SongSearchAdapter.ViewHolder>() {

    private var currentlyPlayingViewHolder: ViewHolder? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumImage: ImageView = itemView.findViewById(R.id.albumImage)
        val songName: TextView = itemView.findViewById(R.id.songName)
        val artistName: TextView = itemView.findViewById(R.id.artistName)
        val previewButton: ImageButton = itemView.findViewById(R.id.previewButton)
        val explicitIcon: ImageView = itemView.findViewById(R.id.explicitIcon)
        var isPlaying: Boolean = false

        init {
            previewButton.setOnClickListener {
                if (isPlaying) {
                    stopAudioPlayback()
                    previewButton.setImageResource(android.R.drawable.ic_media_play)
                    isPlaying = false
                    currentlyPlayingViewHolder = null
                } else {
                    currentlyPlayingViewHolder?.let {
                        it.previewButton.setImageResource(android.R.drawable.ic_media_play)
                        it.isPlaying = false
                    }
                    if (songModels[adapterPosition].previewUrl != null) {
                        MediaPlayerManager.playAudio(songModels[adapterPosition].previewUrl!!)
                        MediaPlayerManager.setOnCompletionListener {
                            previewButton.setImageResource(android.R.drawable.ic_media_play)
                            isPlaying = false
                            currentlyPlayingViewHolder = null
                        }
                    }
                    previewButton.setImageResource(android.R.drawable.ic_media_pause)
                    isPlaying = true
                    currentlyPlayingViewHolder = this
                }
            }
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    fun stopAudioPlayback() {
        currentlyPlayingViewHolder?.let {
            MediaPlayerManager.stopAudio()
            it.previewButton.setImageResource(android.R.drawable.ic_media_play)
            it.isPlaying = false
            currentlyPlayingViewHolder = null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_recycler_view_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = songModels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //
        val imageLoader = ImageLoader(holder.itemView.context)
        imageLoader.loadImage(songModels[position].albumImage, holder.albumImage)
        holder.songName.text = songModels[position].songName
        holder.artistName.text = songModels[position].artistName
        holder.previewButton.isVisible = songModels[position].previewUrl != null
        holder.explicitIcon.isVisible = songModels[position].isExplicit

        holder.previewButton.setImageResource(
            if (holder.isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        )
    }


}
