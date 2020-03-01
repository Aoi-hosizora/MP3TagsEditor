package com.aoihosizora.mp3tagseditor.model

data class Metadata(
    val title: String,
    val artist: String,
    val album: String,
    val year: String,
    val track: String,
    val genre: Int,
    val albumArtist: String,
    val composer: String
)
