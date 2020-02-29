package com.aoihosizora.mp3tagseditor.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityMediaMediaPresenter
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityTagsPresenter
import com.aoihosizora.mp3tagseditor.util.CoverUtil
import com.aoihosizora.mp3tagseditor.util.PathUtil
import kotlinx.android.synthetic.main.activity_main.*
import rx_activity_result2.RxActivityResult
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), IContextHelper, MainActivityContract.View {

    override val mediaPresenter = MainActivityMediaMediaPresenter(this)
    override val tagsPresenter = MainActivityTagsPresenter(this)

    companion object {
        private val ALL_PERMISSION = listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_PERMISSION_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()
        initView()
        initListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
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
    }

    private fun initListener() {
        btn_open.setOnClickListener(onBtnOpenClicked)
        btn_close.setOnClickListener(onBtnCloseClicked)
        btn_switch.setOnClickListener(onBtnSwitchClicked)
        seekbar.setOnSeekBarChangeListener(onSeekBarChange)

        btn_cover.setOnClickListener(onBtnCoverClicked)
        btn_save.setOnClickListener(onBtnSaveClicked)
        btn_restore.setOnClickListener(onBtnRestoreClicked)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            R.id.menu_video -> onMenuVideoClicked()
        }
        return true
    }

    private val onBtnOpenClicked: (View) -> Unit = {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.type = "audio/*"
        RxActivityResult.on(this).startIntent(intent).subscribe { r ->
            if (r.resultCode() == Activity.RESULT_OK) {
                r.data().data?.let {
                    initView(true)
                    loadMusic(it)
                }
            }
        }
    }

    private val onBtnCloseClicked: (View) -> Unit = {
        mediaPresenter.release()
        initView(false)
    }

    private val onBtnSwitchClicked: (View) -> Unit = {
        mediaPresenter.switch()
    }

    private fun loadMusic(uri: Uri) {
        val filepath = PathUtil.getFilePathByUri(this, uri)
        if (filepath.isBlank()) {
            showAlert("Failed", "File not found.")
            initView(false)
            return
        }

        val filename = filepath.split(File.separator).last()
        val mb = File(filepath).length() / 1024 / 1024.0
        txt_file.text = String.format("Opening: %s (%.2fMB)", filename, mb)
        try {
            tagsPresenter.load(filepath)
            mediaPresenter.setup(this, uri)
            mediaPresenter.play()
        } catch (ex: Exception) {
            ex.printStackTrace()
            showAlert("Failed", "Failed to prepare music file.")
            initView(false)
        }
    }

    private val onSeekBarChange = object : SeekBar.OnSeekBarChangeListener {

        override fun onStartTrackingTouch(seekBar: SeekBar?) = mediaPresenter.seekStart()

        override fun onStopTrackingTouch(seekBar: SeekBar?) = mediaPresenter.seekStop(seekbar.progress)

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = mediaPresenter.seekChanging(progress)
    }

    override fun onPause() { // hide or locked
        super.onPause()
        mediaPresenter.pause()
    }

    override fun onDestroy() {
        mediaPresenter.release()
        super.onDestroy()
    }

    override fun setupSeekbar(progress: Int, max: Int) {
        seekbar.progress = progress
        seekbar.max = max
    }

    override fun updateSeekbar(now: String, total: String) {
        txt_music.text = String.format("%s / %s", now, total)
    }

    override fun switchBtnIcon(isPlaying: Boolean) {
        if (isPlaying) {
            btn_switch.setImageResource(R.drawable.ic_pause_accent_24dp)
        } else {
            btn_switch.setImageResource(R.drawable.ic_play_accent_24dp)
        }
    }

    override fun loadTags(title: String, artist: String, album: String) {
        edt_title.setText(title)
        edt_artist.setText(artist)
        edt_album.setText(album)
    }

    override fun loadCover(cover: Bitmap?) {
        if (cover != null) {
            bm_cover.setImageBitmap(cover)
            txt_cover_size.text = String.format("%dx%d %dKB", cover.width, cover.height, cover.allocationByteCount / 1024)
        } else {
            bm_cover.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private val onBtnCoverClicked: (View) -> Unit = {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        RxActivityResult.on(this).startIntent(intent).subscribe { r ->
            if (r.resultCode() == Activity.RESULT_OK) {
                r.data().data?.let {
                    bm_cover.setImageURI(it)
                }
            }
        }
    }

    private val onBtnRestoreClicked: (View) -> Unit = {
        tagsPresenter.restore()
    }

    private val onBtnSaveClicked: (View) -> Unit = {
        val filename = tagsPresenter.getFilename()
        showInputDlg(title = "Filename", text = filename, posText = "Save", posClick = { _, _, text ->
            if (text == filename) {
                showAlert("Failed", "New filename couldn't be the same")
            } else {
                try {
                    tagsPresenter.save(text, edt_title.text.toString(), edt_artist.text.toString(), edt_album.text.toString(), CoverUtil.getBitmapFromImageView(bm_cover))
                    showAlert("Success", String.format("Success to save %s.", text))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    String.format("Failed", String.format("Failed to save %s.", text))
                }
            }
        }, negText = "Cancel")
    }

    private val onMenuVideoClicked: () -> Unit = {
        startActivity(Intent(this, VideoActivity::class.java))
    }

    private fun checkPermission() {
        val requirePermission: List<String> = ALL_PERMISSION.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (requirePermission.isNotEmpty()) {
            showAlert(
                title = "Permission", message = "This app need read and write storage permission.",
                posText = "OK", posListener = { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, requirePermission.toTypedArray(),
                        REQUEST_PERMISSION_CODE
                    )
                }
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
