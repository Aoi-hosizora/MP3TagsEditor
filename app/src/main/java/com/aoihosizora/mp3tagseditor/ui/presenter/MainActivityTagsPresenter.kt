package com.aoihosizora.mp3tagseditor.ui.presenter

import android.graphics.Bitmap
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
            view.loadTags(it.title ?: "", it.artist ?: "", it.album ?: "")
            if (it.albumImage != null && it.albumImage.isNotEmpty()) {
                view.loadCover(ImageUtil.getBitmapFromByteArray(it.albumImage))
            } else {
                view.loadCover(null)
            }
        }
    }

    override fun setCover(bitmap: Bitmap?) {
        mp3File?.id3v2Tag?.let {
            if (bitmap == null) {
                it.clearAlbumImage()
            } else {
                it.setAlbumImage(ImageUtil.getJpegByteArrayFromBitmap(bitmap), "image/jpg")
            }
        }
    }

    override fun save(filename: String, title: String, artist: String, album: String): Boolean {
        mp3File?.let {
            it.id3v2Tag.title = title
            it.id3v2Tag.artist = artist
            it.id3v2Tag.album = album
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
