package com.aoihosizora.mp3tagseditor.ui.presenter

import android.content.Intent
import com.aoihosizora.mp3tagseditor.ui.contract.VideoActivityContract
import com.aoihosizora.mp3tagseditor.util.PathUtil
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg

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

    override fun run(command: String) {
        Config.enableLogCallback {
            view.runOnUiThread(Runnable { view.updateOutput(it.text) })
        }
        view.startRun(command)
        Thread(Runnable {
            val code = FFmpeg.execute(command)
            view.runOnUiThread(Runnable {
                when (code) {
                    Config.RETURN_CODE_SUCCESS -> view.finishRun(true, Config.getLastCommandOutput())
                    Config.RETURN_CODE_CANCEL -> view.finishRun(true, "Execute canceled.")
                    else -> view.finishRun(false, Config.getLastCommandOutput())
                }
            })
        }).start()
    }

    override fun toMp3() {
        val filenameWithoutExt = PathUtil.getFilenameWithoutExt(PathUtil.getFilenameFromPath(getPath()))
        view.setScript("-i \"${getPath()}\" \"$filenameWithoutExt.mp3\"")
    }
}
