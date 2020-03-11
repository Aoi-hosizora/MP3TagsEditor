@file:Suppress("SetTextI18n")

package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.FFmpegActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.FFmpegActivityPresenter
import com.aoihosizora.mp3tagseditor.util.PathUtil
import kotlinx.android.synthetic.main.activity_ffmpeg.*
import rx_activity_result2.RxActivityResult

class FFmpegActivity : AppCompatActivity(), IContextHelper, FFmpegActivityContract.View {

    override val presenter = FFmpegActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    private fun initView() {
        txt_output.movementMethod = ScrollingMovementMethod()

        btn_run.setOnClickListener(onBtnRunClicked)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_ffmpeg_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_reference -> onMenuReferenceClicked()
        }
        return true
    }

    private val onMenuReferenceClicked: () -> Unit = {
        RxActivityResult.on(this).startIntent(openAudioVideoIntent()).subscribe { r ->
            if (r.resultCode() != Activity.RESULT_OK) {
                return@subscribe
            }
            r.data().data?.let {
                val path = PathUtil.getFilePathByUri(this, it)
                showAlert(
                    title = "Reference", message = "Selected:\n$path.",
                    posText = "Copy", posListener = { _, _ ->
                        copyText(path)
                        showToast("Success to copy")
                    },
                    negText = "Insert", negListener = { _, _ ->
                        edt_command.append(path)
                    },
                    netText = "Cancel", netListener = null
                )
            }
        }
    }

    private val onBtnRunClicked: (View) -> Unit = {
        val command = edt_command.text.toString()
        if (command.isBlank()) {
            showAlert("Failed", "Empty script!")
        } else {
            presenter.run(command)
        }
    }

    override fun startRun(command: String) {
        view_result.startRun()

        edt_command.isEnabled = false
        btn_run.isEnabled = false
        txt_output.text = ""
    }

    override fun finishRun(isSuccess: Boolean, message: String) {
        if (isSuccess) {
            view_result.success()
        } else {
            view_result.failed()
        }

        edt_command.isEnabled = true
        btn_run.isEnabled = true
    }

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
