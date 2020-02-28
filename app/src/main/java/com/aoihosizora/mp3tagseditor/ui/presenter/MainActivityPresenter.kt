package com.aoihosizora.mp3tagseditor.ui.presenter

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract

class MainActivityPresenter(
    override val view: MainActivityContract.View
) : MainActivityContract.Presenter {

    private var isChanging = false
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun setup(context: Context, uri: Uri) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.prepare()
        mediaPlayer.setOnCompletionListener {
            view.switchBtnIcon(false)
        }
    }

    override fun switch() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            view.switchBtnIcon(false)
        } else {
            mediaPlayer.start()
            view.switchBtnIcon(true)
            Thread(looperThread).start()
        }
    }

    override fun play() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            view.switchBtnIcon(true)
            Thread(looperThread).start()
        }
    }

    override fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            view.switchBtnIcon(false)
        }
    }

    override fun stop() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            view.switchBtnIcon(false)
        }
    }

    override fun release() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            view.switchBtnIcon(false)
        }
        mediaPlayer.release()
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
        while (!isChanging && mediaPlayer.isPlaying) {
            val curr = mediaPlayer.currentPosition
            val du = mediaPlayer.duration
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
    }

    override fun seekStart() {
        isChanging = true
    }

    override fun seekStop(progress: Int) {
        mediaPlayer.seekTo(progress)
        isChanging = false
        Thread(looperThread).start()
    }

    override fun seekChanging(progress: Int) {
        view.updateSeekbar(parseProgress(progress), parseProgress(mediaPlayer.duration))
    }
}
