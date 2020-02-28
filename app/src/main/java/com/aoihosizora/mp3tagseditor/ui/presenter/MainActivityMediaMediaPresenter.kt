package com.aoihosizora.mp3tagseditor.ui.presenter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract

class MainActivityMediaMediaPresenter(
    override val view: MainActivityContract.View
) : MainActivityContract.MediaPresenter {

    private var isChanging = false
    private var mediaPlayer: MediaPlayer? = null

    override fun setup(context: Context, uri: Uri) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.let {
            it.setDataSource(context, uri)
            it.prepare()
            it.setOnCompletionListener {
                view.switchBtnIcon(false)
            }
        }
    }

    override fun play() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                view.switchBtnIcon(true)
                Thread(looperThread).start()
            }
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                view.switchBtnIcon(false)
            }
        }
    }

    override fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                view.switchBtnIcon(false)
            }
        }
    }

    override fun switch() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    override fun release() {
        stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun parseProgress(progress: Int): String {
        val allSeconds = progress / 1000
        val hour = allSeconds / 3600
        val minute = allSeconds % 3600 / 60
        val second = allSeconds % 60
        return if (hour == 0) {
            String.format("%02d:%02d", minute, second)
        } else {
            String.format("%02d:%02d:%02d", hour, minute, second)
        }
    }

    private val looperThread = Runnable {
        try {
            while (!isChanging && mediaPlayer != null && mediaPlayer!!.isPlaying) {
                val curr = mediaPlayer!!.currentPosition
                val du = mediaPlayer!!.duration
                if (curr < du) {
                    view.runOnUiThread(Runnable {
                        view.setupSeekbar(curr, du)
                        view.updateSeekbar(parseProgress(curr), parseProgress(du))
                    })
                    try {
                        Thread.sleep(100)
                    } catch (ex: InterruptedException) {
                        ex.printStackTrace()
                    }
                } else {
                    break
                }
            }
        } catch (ex: IllegalStateException) {
            ex.printStackTrace() // android.media.MediaPlayer.isPlaying(Native Method)
        }
    }

    override fun seekStart() {
        isChanging = true
    }

    override fun seekStop(progress: Int) {
        mediaPlayer?.let {
            it.seekTo(progress)
            isChanging = false
            Thread(looperThread).start()
        }
    }

    override fun seekChanging(progress: Int) {
        mediaPlayer?.let {
            view.updateSeekbar(parseProgress(progress), parseProgress(it.duration))
        }
    }
}
