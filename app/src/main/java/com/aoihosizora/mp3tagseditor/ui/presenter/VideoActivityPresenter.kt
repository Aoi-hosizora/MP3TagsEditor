package com.aoihosizora.mp3tagseditor.ui.presenter

import android.content.Context
import android.content.Intent
import com.aoihosizora.mp3tagseditor.ui.contract.VideoActivityContract
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException

class VideoActivityPresenter(
    override val view: VideoActivityContract.View
) : VideoActivityContract.Presenter {

    companion object {
        const val INTENT_PATH = "INTENT_PATH"
    }

    private var videoPath: String = ""

    override fun initData(intent: Intent) {
        videoPath = intent.getStringExtra(INTENT_PATH)
    }

    override fun getPath(): String {
        return videoPath
    }

    override fun run(context: Context, command: String) {
        val ffmpeg = FFmpeg.getInstance(context)
        try {
            ffmpeg.execute(arrayOf(command), object : ExecuteBinaryResponseHandler() {

                override fun onStart() {
                    view.startRun(command)
                }

                override fun onProgress(message: String?) {
                    message?.let { view.updateOutput(message) }
                }

                override fun onSuccess(message: String?) {
                    message?.let { view.finishRun(true, message) }
                }

                override fun onFailure(message: String?) {
                    message?.let { view.finishRun(false, message) }
                }
            })
        } catch (ex: FFmpegCommandAlreadyRunningException) {
            view.finishRun(false, "command is already running")
        }
    }
}
