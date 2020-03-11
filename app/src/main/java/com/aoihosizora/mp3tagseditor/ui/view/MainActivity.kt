@file:Suppress("SetTextI18n")

package com.aoihosizora.mp3tagseditor.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import com.aoihosizora.mp3tagseditor.MyApplication
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.model.Metadata
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityMediaPresenter
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityTagsPresenter
import com.aoihosizora.mp3tagseditor.ui.presenter.VideoActivityPresenter
import com.aoihosizora.mp3tagseditor.util.ImageUtil
import com.aoihosizora.mp3tagseditor.util.PathUtil
import kotlinx.android.synthetic.main.activity_main.*
import rx_activity_result2.RxActivityResult
import java.io.File


class MainActivity : AppCompatActivity(), IContextHelper, MainActivityContract.View {

    override val mediaPresenter = MainActivityMediaPresenter(this)
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
        txt_filename.visibility = def
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

        btn_choose_cover.setOnClickListener(onBtnChooseCoverClicked)
        btn_save.setOnClickListener(onBtnSaveClicked)
        btn_restore.setOnClickListener(onBtnRestoreClicked)

        val popupMenu = PopupMenu(this, btn_options)
        menuInflater.inflate(R.menu.activity_main_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_crop -> onMenuCropCoverClicked()
                R.id.menu_remove -> onMenuRemoveCoverClicked()
                R.id.menu_save -> onMenuSaveCoverClicked()
            }
            true
        }
        btn_options.setOnClickListener { popupMenu.show() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            R.id.menu_video -> onMenuVideoClicked()
            R.id.menu_ffmpeg -> onMenuFFmpegClicked()
        }
        return true
    }

    private val onBtnOpenClicked: (View) -> Unit = {
        RxActivityResult.on(this).startIntent(openAudioIntent()).subscribe { r ->
            if (r.resultCode() == Activity.RESULT_OK) {
                r.data().data?.let {
                    loadMusic(it)
                }
            }
        }
    }

    private val onBtnCloseClicked: (View) -> Unit = {
        mediaPresenter.release()
        initView(false)
    }

    // region Play

    private val onBtnSwitchClicked: (View) -> Unit = {
        mediaPresenter.switch()
    }

    private fun loadMusic(uri: Uri) {
        val filepath = PathUtil.getFilePathByUri(this, uri)
        if (filepath.isBlank()) {
            showAlert("Failed", "File not found.")
            return
        }
        if (!PathUtil.checkIsAudio(filepath)) {
            showAlert("Failed", "$filepath is not a audio or an unsupported type.")
            return
        }
        initView(true)

        val filename = filepath.split(File.separator).last()
        val mb = File(filepath).length() / 1024 / 1024.0
        txt_filename.text = String.format("Opening: %s (%.2fMB)", filename, mb)
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
        (application as MyApplication).clearObject()
        super.onDestroy()
    }

    override fun setupSeekbar(progress: Int, max: Int) {
        seekbar.progress = progress
        seekbar.max = max
    }

    override fun updateSeekbar(now: String, total: String) {
        txt_music.text = "$now / $total"
    }

    override fun switchBtnIcon(isPlaying: Boolean) {
        if (isPlaying) {
            btn_switch.setImageResource(R.drawable.ic_pause_accent_24dp)
        } else {
            btn_switch.setImageResource(R.drawable.ic_play_accent_24dp)
        }
    }

    // endregion

    override fun loadTags(metadata: Metadata) {
        edt_title.setText(metadata.title)
        edt_artist.setText(metadata.artist)
        edt_album.setText(metadata.album)
        edt_year.setText(metadata.year)
        edt_track.setText(metadata.track)
        edt_genre.setText(metadata.genre.toString())
        edt_album_artist.setText(metadata.albumArtist)
        edt_composer.setText(metadata.composer)
    }

    private val onBtnRestoreClicked: (View) -> Unit = {
        showAlert("Check", "Sure to restore?", posText = "Restore", posListener = { _, _ ->
            tagsPresenter.restore()
        }, negText = "Cancel")
    }

    // region Cover

    /**
     * !!!
     */
    override fun loadCover(cover: Bitmap?) {
        if (cover != null) {
            iv_cover.setImageBitmap(cover)
            btn_options.isEnabled = true
            txt_cover_size.text = "${cover.width} x ${cover.height}"
        } else {
            iv_cover.setImageResource(R.color.transparent)
            btn_options.isEnabled = false
            txt_cover_size.text = "Blank"
        }
    }

    private val onMenuRemoveCoverClicked: () -> Unit = {
        iv_cover.setImageResource(android.R.color.transparent)
        btn_options.isEnabled = false
        txt_cover_size.text = "Blank"
    }

    private val onBtnChooseCoverClicked: (View) -> Unit = {
        RxActivityResult.on(this).startIntent(openImageIntent()).subscribe { r ->
            if (r.resultCode() == Activity.RESULT_OK) {
                r.data().data?.let {
                    val bm = ImageUtil.getBitmapFromUri(contentResolver, it)
                    loadCover(bm)
                }
            }
        }
    }

    private val onMenuCropCoverClicked: () -> Unit = a@{
        val intent = Intent(this, CropActivity::class.java)
        val cover = ImageUtil.getBitmapFromImageView(iv_cover)
        if (cover == null) {
            showAlert("Failed", "Failed to get cover the image.")
            return@a
        }
        (application as MyApplication).setObject(cover)
        try {
            RxActivityResult.on(this).startIntent(intent).subscribe { r ->
                if (r.resultCode() == Activity.RESULT_OK) {
                    val bm = (application as MyApplication).getObject() as? Bitmap
                    loadCover(bm)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val onMenuSaveCoverClicked: () -> Unit = {
        showAlert("Save", "Save cover?", posText = "Save", posListener = a@{ _, _ ->
            val cover = ImageUtil.getBitmapFromImageView(iv_cover)
            if (cover == null) {
                showAlert("Failed", "Failed to get the cover.")
                return@a
            }
            val desc = "${tagsPresenter.getFilenameWithoutExt()}_cover"
            try {
                val path = ImageUtil.saveImage(this, contentResolver, cover, desc, desc)
                showToast("Save path in $path success")
            } catch (ex: Exception) {
                ex.printStackTrace()
                showAlert("Failed", "Failed to save the cover.")
            }
        }, negText = "Cancel")
    }

    // endregion

    private val onBtnSaveClicked: (View) -> Unit = {
        val filename = tagsPresenter.getFilename()
        fun save(path: String, fakeName: String = path) {
            val metadata = Metadata(
                edt_title.text.toString(), edt_artist.text.toString(), edt_album.text.toString(),
                edt_year.text.toString(), edt_track.text.toString(), edt_genre.text.toString().toInt(),
                edt_album_artist.text.toString(), edt_composer.text.toString()
            )
            val ok = tagsPresenter.save(path, metadata, ImageUtil.getBitmapFromImageView(iv_cover))
            if (ok) {
                showToast("Success to save $fakeName")
            } else {
                showAlert("Failed", "Failed to save music $fakeName.")
            }
        }

        showInputDlg(title = "Filename", text = filename, posText = "Save", posClick = { _, _, text ->
            val file = File(text)
            if (file.exists()) {
                showAlert("Save", "File has existed, overwrite it?", posText = "Overwrite", posListener = { _, _ ->
                    val newFilename = "$text-${PathUtil.getTimeUuid()}"
                    save(newFilename, text)
                    file.delete()
                    if (!File(newFilename).renameTo(File(text))) {
                        showAlert("Failed", "Failed to save music $text, temp file have been saved in $newFilename.")
                    }
                }, negText = "Cancel")
            } else {
                save(text)
            }
        }, negText = "Cancel")
    }

    private val onMenuVideoClicked: () -> Unit = {
        RxActivityResult.on(this).startIntent(openVideoIntent()).subscribe { r ->
            if (r.resultCode() == Activity.RESULT_OK) {
                r.data().data?.let {
                    val path = PathUtil.getFilePathByUri(this, it)
                    if (path.isBlank()) {
                        showAlert("Failed", "File not found.")
                        return@let
                    }
                    if (!PathUtil.checkIsVideo(path)) {
                        showAlert("Failed", "$path is not a video or an unsupported type.")
                        return@let
                    }
                    val videoIntent = Intent(this, VideoActivity::class.java)
                    videoIntent.putExtra(VideoActivityPresenter.INTENT_PATH, path)
                    startActivity(videoIntent)
                }
            }
        }
    }

    private val onMenuFFmpegClicked: () -> Unit = {
        val intent = Intent(this, FFmpegActivity::class.java)
        startActivity(intent)
    }

    // region Permission

    private fun checkPermission() {
        val requirePermission: List<String> = ALL_PERMISSION.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (requirePermission.isNotEmpty()) {
            showAlert(
                title = "Permission", message = "This app needs \"read and write storage\" permission.",
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
                    posText = "Close", posListener = { _, _ -> finish() }
                )
            } else {
                showToast("Success to grant permission")
            }
        }
    }

    // endregion
}
