package com.aoihosizora.mp3tagseditor.ui.presenter

import android.graphics.Bitmap
import com.aoihosizora.mp3tagseditor.model.Metadata
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.util.ImageUtil
import com.aoihosizora.mp3tagseditor.util.PathUtil
import com.mpatric.mp3agic.ID3v2
import com.mpatric.mp3agic.ID3v23Tag
import com.mpatric.mp3agic.Mp3File
import java.lang.Exception

class MainActivityTagsPresenter(
    override val view: MainActivityContract.View
) : MainActivityContract.TagsPresenter {

    private var mp3File: Mp3File? = null
    private var tags: ID3v2? = null

    override fun load(path: String) {
        mp3File = Mp3File(path) // No mpegs frames found...
        if (!mp3File!!.hasId3v2Tag()) {
            mp3File!!.id3v2Tag = ID3v23Tag()
        }
        tags = mp3File!!.id3v2Tag
        try {
            restore()
        } catch (ex: Exception) {
            mp3File!!.id3v2Tag = ID3v23Tag()
            tags = mp3File!!.id3v2Tag
            restore()
        }
    }

    override fun restore() {
        tags?.let {
            view.loadTags(
                Metadata(
                    title = it.title ?: "", artist = it.artist ?: "", album = it.album ?: "",
                    year = it.year ?: "", track = it.track ?: "", genre = it.genre,
                    albumArtist = it.albumArtist ?: "", composer = it.composer ?: ""
                )
            )
            if (it.albumImage != null && it.albumImage.isNotEmpty()) {
                view.loadCover(ImageUtil.getBitmapFromByteArray(it.albumImage))
            } else {
                view.loadCover(null)
            }
        }
    }

    override fun save(filename: String, metadata: Metadata, cover: Bitmap?): Boolean {
        mp3File?.let {
            it.id3v2Tag.title = metadata.title
            it.id3v2Tag.artist = metadata.artist
            it.id3v2Tag.album = metadata.album
            it.id3v2Tag.year = metadata.year
            it.id3v2Tag.track = metadata.track
            it.id3v2Tag.genre = metadata.genre
            it.id3v2Tag.albumArtist = metadata.albumArtist
            it.id3v2Tag.composer = metadata.composer

            if (cover == null) {
                it.id3v2Tag.clearAlbumImage()
            } else {
                it.id3v2Tag.setAlbumImage(ImageUtil.getJpegByteArrayFromBitmap(cover), "image/jpg")
            }

            return try {
                it.save(filename)
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        }
        return false
    }

    override fun getFilename(): String {
        return mp3File?.filename ?: ""
    }

    override fun getFilenameWithoutExt(): String {
        return PathUtil.getFilenameWithoutExt(getFilename())
    }
}
