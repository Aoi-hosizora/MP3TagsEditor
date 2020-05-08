package com.aoihosizora.mp3tagseditor.ui.contract

interface CropActivityContract {

    interface View {
        val presenter: Presenter
    }

    interface Presenter {
        val view: View
    }
}