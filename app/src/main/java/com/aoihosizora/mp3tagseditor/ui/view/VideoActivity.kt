package com.aoihosizora.mp3tagseditor.ui.view

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.VideoActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.VideoActivityPresenter

class VideoActivity : AppCompatActivity(), IContextHelper, VideoActivityContract.View {

    override val presenter = VideoActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_video)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}
