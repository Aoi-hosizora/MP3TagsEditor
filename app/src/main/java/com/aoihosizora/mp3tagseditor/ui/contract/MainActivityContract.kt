package com.aoihosizora.mp3tagseditor.ui.contract

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.mpatric.mp3agic.ID3v2

interface MainActivityContract {

    interface View {
        val mediaPresenter: MediaPresenter
        val tagsPresenter: TagsPresenter

        fun runOnUiThread(action: Runnable)
        fun setupSeekbar(progress: Int, max: Int)
        fun updateSeekbar(now: String, total: String)
        fun switchBtnIcon(isPlaying: Boolean)

        fun loadTags(title: String, artist: String, album: String)
        fun loadCover(cover: Bitmap?)
    }

    interface MediaPresenter {
        val view: View

        fun setup(context: Context, uri: Uri)
        fun switch()
        fun play()
        fun pause()
        fun stop()
        fun release()

        fun seekStart()
        fun seekStop(progress: Int)
        fun seekChanging(progress: Int)
    }

    interface TagsPresenter {
        val view: View

        fun load(path: String)
        fun restore()
        fun getFilename(): String
        fun getFilenameWithoutExt(): String
        fun setCover(bitmap: Bitmap?)
        fun save(filename: String, title: String, artist: String, album: String): Boolean
    }
}
