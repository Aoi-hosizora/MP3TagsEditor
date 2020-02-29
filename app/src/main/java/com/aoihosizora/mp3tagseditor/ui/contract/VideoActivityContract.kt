package com.aoihosizora.mp3tagseditor.ui.contract

interface VideoActivityContract {

    interface View {
        val presenter: Presenter
    }

    interface Presenter {
        val view: View
    }
}