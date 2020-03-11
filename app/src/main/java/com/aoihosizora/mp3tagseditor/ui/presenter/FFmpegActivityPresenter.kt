package com.aoihosizora.mp3tagseditor.ui.presenter

import com.aoihosizora.mp3tagseditor.ui.contract.FFmpegActivityContract
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg

class FFmpegActivityPresenter(
    override val view: FFmpegActivityContract.View
) : FFmpegActivityContract.Presenter {

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
}
