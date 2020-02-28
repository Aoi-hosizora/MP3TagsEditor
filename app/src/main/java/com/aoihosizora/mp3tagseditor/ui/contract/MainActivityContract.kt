package com.aoihosizora.mp3tagseditor.ui.contract

import android.content.Context
import android.net.Uri

interface MainActivityContract {

    interface View {
        val presenter: Presenter

        fun runOnUiThread(action: Runnable)
        fun setupSeekbar(progress: Int, max: Int)
        fun updateSeekbar(now: String, total: String)
        fun switchBtnIcon(isPlaying: Boolean)
    }

    interface Presenter {
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
}
