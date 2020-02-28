package com.aoihosizora.mp3tagseditor.ui.presenter

import android.graphics.Bitmap
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.util.CoverUtil
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
        mp3File = Mp3File(path)
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
                view.loadCover(CoverUtil.getBitmapFromByteArray(it.albumImage))
            } else {
                view.loadCover(null)
            }
        }
    }

    override fun save(filename: String, title: String, artist: String, album: String, cover: Bitmap?) {
        mp3File?.let {
            it.id3v2Tag.title = title
            it.id3v2Tag.artist = artist
            it.id3v2Tag.album = album
            cover?.let { bm ->
                it.id3v2Tag.setAlbumImage(CoverUtil.getJpegByteArrayFromBitmap(bm), "image/jpg")
            }
            it.save(filename)
        }
    }

    override fun getFilename(): String {
        return mp3File?.filename ?: ""
    }
}