package com.aoihosizora.mp3tagseditor.ui.contract

import android.content.Intent

interface VideoActivityContract {

    interface View {
        val presenter: Presenter

        fun setScript(command: String)
        fun startRun(command: String)
        fun finishRun(isSuccess: Boolean, message: String)
        fun updateOutput(content: String)
    }

    interface Presenter {
        val view: View

        fun initData(intent: Intent)
        fun getPath(): String
        fun run(command: String)
        fun toMp3()
    }
}
