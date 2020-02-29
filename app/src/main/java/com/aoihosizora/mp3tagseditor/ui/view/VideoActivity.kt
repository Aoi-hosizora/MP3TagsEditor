package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        txt_filename.text = String.format("Opening: %s", presenter.getPath())
        btn_run.setOnClickListener(onBtnRunClicked)
    }

    private val onBtnRunClicked: (View) -> Unit = {
        val command = edt_script.text.toString()
        if (command.isBlank()) {
            showAlert("Failed", "Couldn't run empty script")
        } else {
            presenter.run(this, command)

        }
    }

    private var progress: Dialog? = null

    override fun startRun(command: String) {
        edt_script.isEnabled = false
        txt_output.text = ""
        progress = showProgress(this, command, false)
    }

    override fun finishRun(isSuccess: Boolean, message: String) {
        edt_script.isEnabled = true
        progress?.dismiss()
        showAlert(if (isSuccess) "Success" else "Failed", message)
    }

    override fun updateOutput(content: String) {
        txt_output.text = String.format("%s\n%s", txt_output.text, content)
    }
}
