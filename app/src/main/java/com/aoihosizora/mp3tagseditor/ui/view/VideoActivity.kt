@file:Suppress("SetTextI18n")

package com.aoihosizora.mp3tagseditor.ui.view

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

        title = "Video converter"
        presenter.initData(intent)
        initView()
    }

    private fun initView() {
        txt_filename.text = "Opening: ${presenter.getPath()}"
        txt_output.movementMethod = ScrollingMovementMethod()

        btn_run.setOnClickListener(onBtnRunClicked)
        btn_copy.setOnClickListener(onBtnCopyClicked)
        btn_to_mp3.setOnClickListener { presenter.toMusic("mp3") }
        btn_to_m4a.setOnClickListener { presenter.toMusic("m4a") }
        btn_help.setOnClickListener(onBtnHelpClicked)
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

    /**
     * btn: run script
     */
    private val onBtnRunClicked: (View) -> Unit = {
        val command = edt_command.text.toString()
        if (command.isBlank()) {
            showAlert("Failed", "Empty script!")
        } else {
            presenter.run(command)
        }
    }

    /**
     * btn: copy path
     */
    private val onBtnCopyClicked: (View) -> Unit = {
        copyText(presenter.getPath())
        showToast("Success to copy")
    }

    /**
     * btn: open help
     */
    private val onBtnHelpClicked: (View) -> Unit = {
        openBrowser("http://ffmpeg.org/ffmpeg.html")
    }

    /**
     * view: set script
     */
    override fun setScript(command: String) {
        edt_command.setText(command)
    }

    /**
     * view: start run
     */
    override fun startRun(command: String) {
        view_result.startRun()

        edt_command.isEnabled = false
        btn_run.isEnabled = false
        txt_output.text = ""
    }

    /**
     * view: finish run
     */
    override fun finishRun(isSuccess: Boolean, message: String) {
        if (isSuccess) {
            view_result.success()
        } else {
            view_result.failed()
        }

        edt_command.isEnabled = true
        btn_run.isEnabled = true
    }

    /**
     * view: update output
     */
    override fun updateOutput(content: String) {
        txt_output.append(content)
        val scrollAmount = txt_output.layout.getLineTop(txt_output.lineCount) - txt_output.height
        if (scrollAmount > 0) {
            txt_output.scrollTo(0, scrollAmount)
        } else {
            txt_output.scrollTo(0, 0)
        }
    }
}
