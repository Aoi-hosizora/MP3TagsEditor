package com.aoihosizora.mp3tagseditor.ui.contract

interface FFmpegActivityContract {

    interface View {
        val presenter: Presenter

        fun runOnUiThread(action: Runnable)
        fun startRun(command: String)
        fun finishRun(isSuccess: Boolean, message: String)
        fun updateOutput(content: String)
    }

    interface Presenter {
        val view: View

        fun run(command: String)
    }
}