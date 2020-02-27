package com.aoihosizora.mp3tagseditor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import com.aoihosizora.mp3tagseditor.R
import kotlinx.android.synthetic.main.activity_main.*
import rx_activity_result2.RxActivityResult
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), IContextHelper {

    companion object {
        private val ALL_PERMISSION = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()
        initView()
    }

    private fun initView(hasFile: Boolean = false) {
        val def = if (hasFile) View.VISIBLE else View.GONE
        val vis = if (!hasFile) View.VISIBLE else View.GONE

        txt_open.visibility = vis
        btn_open.visibility = vis
        txt_file.visibility = def
        btn_close.visibility = def
        ll_music.visibility = def
        view_divide_head.visibility = def
        ll_main.visibility = def
        view_divide_bottom.visibility = def
        ll_bottom.visibility = def

        btn_open.setOnClickListener(onBtnOpenClicked)
        btn_close.setOnClickListener(onBtnCloseClicked)
        btn_save.setOnClickListener(onBtnSaveClicked)
        btn_play_pause.setOnClickListener(onBtnPlayPauseClicked)
        seekbar.setOnSeekBarChangeListener(onSeekBarChange)
    }

    private val onBtnOpenClicked: (View) -> Unit = {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.type = "audio/*"
        RxActivityResult.on(this).startIntent(intent)
            .subscribe { r ->
                if (r.resultCode() != Activity.RESULT_OK) {
                    return@subscribe
                }
                r.data().data?.let {
                    initView(true)
                    loadMusic(it)
                }
            }
    }

    private val onBtnCloseClicked: (View) -> Unit = {
        initView(false)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    private val onBtnSaveClicked: (View) -> Unit = {
        showToast("save")
    }

    private val mediaPlayer = MediaPlayer()

    /**
     * Is seeking?
     */
    private var isChanging = false

    override fun onPause() { // hide or locked
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        super.onDestroy()
    }

    private val looperThread = Runnable {
        var curr: Int
        while (!isChanging && mediaPlayer.isPlaying) {
            curr = mediaPlayer.currentPosition
            if (curr < mediaPlayer.duration) {
                runOnUiThread {
                    seekbar.progress = curr
                    updateProgress(mediaPlayer.currentPosition, mediaPlayer.duration)
                }
                try {
                    Thread.sleep(100)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            } else {
                break
            }
        }
    }

    private val onSeekBarChange = object : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            isChanging = true
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            mediaPlayer.seekTo(seekbar.progress)
            isChanging = false
            Thread(looperThread).start()
        }

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            updateProgress(progress, mediaPlayer.duration)
        }
    }

    private fun loadMusic(uri: Uri) {
        uri.path?.let {
            val filename = it.split(File.separator).last()
            val mb = File(it).length() / 1024 / 1024.0
            txt_file.text = String.format("Opening: %s (%.2fMB)", filename, mb)
        }
        try {
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepare()
        } catch (ex: Exception) {
            showAlert("Failed", "Failed to prepare music file.")
            return
        }
        mediaPlayer.setOnCompletionListener { btnPlayIcon() }
        mediaPlayer.start()
        seekbar.max = mediaPlayer.duration
        seekbar.progress = mediaPlayer.currentPosition
        Thread(looperThread).start()
        btnPauseIcon()
    }

    private fun updateProgress(position: Int, du: Int) {
        val func: (Int) -> String = a@{
            val allSeconds = it / 1000
            val hour = allSeconds / 3600
            val minute = allSeconds % 3600 / 60
            val second = allSeconds % 60
            return@a if (hour == 0) {
                String.format("%02d:%02d", minute, second)
            } else {
                String.format("%02d:%02d:%02d", hour, minute, second)
            }
        }
        txt_music.text = String.format("%s / %s", func(position), func(du))
    }

    private val onBtnPlayPauseClicked: (View) -> Unit = {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
            Thread(looperThread).start()
        }

        if (mediaPlayer.isPlaying) {
            btnPlayIcon()
        } else {
            btnPauseIcon()
        }
    }

    private fun btnPlayIcon() {
        btn_play_pause.setImageResource(R.drawable.ic_play_accent_24dp)
    }

    private fun btnPauseIcon() {
        btn_play_pause.setImageResource(R.drawable.ic_pause_accent_24dp)
    }

    private fun checkPermission() {
        val requirePermission: List<String> = ALL_PERMISSION.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (requirePermission.isNotEmpty()) {
            showAlert(
                title = "Permission", message = "This app need read and write storage permission.",
                posText = "OK", posListener = { _, _ -> ActivityCompat.requestPermissions(this, requirePermission.toTypedArray(), REQUEST_PERMISSION_CODE) }
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showAlert(
                    title = "Failed", message = "Grant permission failed.",
                    posText = "Close", posListener = { _, _ -> exitProcess(1) }
                )
            } else {
                showToast("Success to grant permission")
            }
        }
    }
}
