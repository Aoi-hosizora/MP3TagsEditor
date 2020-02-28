package com.aoihosizora.mp3tagseditor.ui.presenter

import android.graphics.Bitmap
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.util.CoverUtil
import com.mpatric.mp3agic.ID3v2
import com.mpatric.mp3agic.Mp3File

class MainActivityTagsPresenter(
    override val view: MainActivityContract.View
) : MainActivityContract.TagsPresenter {

    private var mp3File: Mp3File? = null
    private var tags: ID3v2? = null

    override fun load(path: String) {
        mp3File = Mp3File(path)
        tags = mp3File!!.id3v2Tag
        tags?.let {
            view.loadTags(it.title, it.artist, it.album)
            if (it.albumImage.isNotEmpty()) {
                view.loadCover(CoverUtil.getBitmapFromByteArray(it.albumImage))
            } else {
                view.loadCover(null)
            }
        }
    }

    override fun save(title: String, artist: String, album: String, cover: Bitmap?) {
        mp3File?.let {
            it.id3v2Tag.title = title
            it.id3v2Tag.artist = artist
            it.id3v2Tag.album = album
            cover?.let { b ->
                it.id3v2Tag.setAlbumImage(CoverUtil.getByteArrayFromBitmap(b), it.id3v2Tag.albumImageMimeType)
            }
            it.save(it.filename)
        }
    }
}
