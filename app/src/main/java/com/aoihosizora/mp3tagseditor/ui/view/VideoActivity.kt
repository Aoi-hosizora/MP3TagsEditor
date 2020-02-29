@file:Suppress("SetTextI18n")

package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.MenuItem
import android.view.View
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.VideoActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.VideoActivityPresenter
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity(), IContextHelper, VideoActivityContract.View {

    override val presenter = VideoActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.initData(intent)
        initView()
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

    private fun initView() {
        txt_filename.text = "Opening: ${presenter.getPath()}"
        txt_output.movementMethod = ScrollingMovementMethod()
        btn_run.setOnClickListener(onBtnRunClicked)
        btn_copy.setOnClickListener(onBtnCopyClicked)
        btn_to_mp3.setOnClickListener { presenter.toMp3() }
    }

    private val onBtnRunClicked: (View) -> Unit = {
        val command = edt_script.text.toString()
        if (command.isBlank()) {
            showAlert("Failed", "Empty script!")
        } else {
            presenter.run(command)

        }
    }

    private val onBtnCopyClicked: (View) -> Unit = {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Label", presenter.getPath())
        cm.primaryClip = clipData
        showToast("Success to copy")
    }

    override fun setScript(command: String) {
        edt_script.setText(command)
    }

    private var progress: Dialog? = null

    override fun startRun(command: String) {
        edt_script.isEnabled = false
        txt_output.text = "-i ${presenter.getPath()}"
        progress = showProgress(this, command, false)
    }

    override fun finishRun(isSuccess: Boolean, message: String) {
        edt_script.isEnabled = true
        progress?.dismiss()
        txt_output.text = message
    }

    override fun updateOutput(content: String) {
        // txt_output.text = "${txt_output.text}\n$content"
    }
}
