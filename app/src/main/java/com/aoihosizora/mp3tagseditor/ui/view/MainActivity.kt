package com.aoihosizora.mp3tagseditor.ui.view

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.MainActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityMediaMediaPresenter
import com.aoihosizora.mp3tagseditor.ui.presenter.MainActivityTagsPresenter
import com.aoihosizora.mp3tagseditor.util.CoverUtil
import com.aoihosizora.mp3tagseditor.util.PathUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onSeekBarChangeListener
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout {
            setPadding(R.dimen.pageMarginHorizontal, R.dimen.pageMarginVertical, R.dimen.pageMarginHorizontal, R.dimen.pageMarginVertical)
            dividerDrawable = getDrawable(R.drawable.spacer_medium)
            showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

            linearLayout {
                id = R.id.ll_head
                dividerDrawable = getDrawable(R.drawable.spacer_medium)
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                textView("No music...") {
                    id = R.id.txt_open
                }.lparams(width = 0) { weight = 1f }
                textView("Opening: %s") {
                    id = R.id.txt_file
                }.lparams(width = 0) { weight = 1f }
                themedButton("Open file", R.style.Widget_AppCompat_Button_Colored) {
                    id = R.id.btn_open; allCaps = false
                }
                themedButton("Close", R.style.Widget_AppCompat_Button_Colored) {
                    id = R.id.btn_close; allCaps = false
                }
            }.lparams(width = matchParent)

            linearLayout {
                id = R.id.ll_music
                gravity = Gravity.CENTER_VERTICAL
                dividerDrawable = getDrawable(R.drawable.spacer_medium)
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                textView("00:00 / 00:00") {
                    id = R.id.txt_music
                }
                seekBar {
                    id = R.id.seekbar
                }.lparams(width = 0) { weight = 1f }
                themedImageButton(R.drawable.ic_play_accent_24dp, R.style.TextAppearance_AppCompat_Widget_Button_Borderless_Colored) {
                    id = R.id.btn_switch
                }.lparams(width = dip(40), height = dip(40))
            }.lparams(width = matchParent)

            include<View>(R.layout.divider) {
                id = R.id.view_divide_head
            }.lparams(width = matchParent)

            verticalLayout {
                id = R.id.ll_main
                dividerDrawable = getDrawable(R.drawable.spacer_medium)
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                linearLayout {
                    id = R.id.ll_title
                    dividerDrawable = getDrawable(R.drawable.spacer_medium)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                    textView("Title") {
                        id = R.id.txt_title
                    }.lparams(width = resources.getDimension(R.dimen.settingLabelWidth).toInt())
                    editText {
                        id = R.id.edt_title
                        hint = "Song title"
                    }.lparams(width = 0) { weight = 1f }
                }.lparams(width = matchParent)

                linearLayout {
                    id = R.id.ll_artist
                    dividerDrawable = getDrawable(R.drawable.spacer_medium)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                    textView("Artist") {
                        id = R.id.txt_artist
                    }.lparams(width = resources.getDimension(R.dimen.settingLabelWidth).toInt())
                    editText {
                        id = R.id.edt_artist
                        hint = "Song Artist"
                    }.lparams(width = 0) { weight = 1f }
                }.lparams(width = matchParent)

                linearLayout {
                    id = R.id.ll_album
                    dividerDrawable = getDrawable(R.drawable.spacer_medium)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                    textView("Album") {
                        id = R.id.txt_album
                    }.lparams(width = resources.getDimension(R.dimen.settingLabelWidth).toInt())
                    editText {
                        id = R.id.edt_album
                        hint = "Song album"
                    }.lparams(width = 0) { weight = 1f }
                }.lparams(width = matchParent)

                linearLayout {
                    id = R.id.ll_cover
                    dividerDrawable = getDrawable(R.drawable.spacer_medium)
                    showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                    textView("Cover") {
                        id = R.id.txt_cover
                    }.lparams(width = resources.getDimension(R.dimen.settingLabelWidth).toInt())
                    textView("0x0 0KB") {
                        id = R.id.txt_cover_size
                    }.lparams(width = 0) { weight = 1f }
                    themedButton("Replace", R.style.Widget_AppCompat_Button_Colored) {
                        id = R.id.btn_cover; allCaps = false
                    }
                }.lparams(width = matchParent)

                imageView(R.drawable.ic_launcher_background) {
                    id = R.id.bm_cover
                }
            }.lparams(height = 0, width = matchParent) { weight = 1f }

            include<View>(R.layout.divider) {
                id = R.id.view_divide_bottom
            }.lparams(width = matchParent)

            linearLayout {
                id = R.id.ll_bottom
                gravity = Gravity.CENTER_HORIZONTAL
                dividerDrawable = getDrawable(R.drawable.spacer_medium)
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE

                themedButton("Restore", R.style.Widget_AppCompat_Button_Colored) {
                    id = R.id.btn_restore; allCaps = false
                }
                themedButton("Save", R.style.Widget_AppCompat_Button_Colored) {
                    id = R.id.btn_save; allCaps = false
                }
            }.lparams(width = matchParent)
        }

        // setContentView(R.layout.activity_main)

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
        btn_switch.setOnClickListener(onBtnSwitchClicked)
        seekbar.onSeekBarChangeListener {
            onStartTrackingTouch { mediaPresenter.seekStart() }
            onStopTrackingTouch { mediaPresenter.seekStop(seekbar.progress) }
            onProgressChanged { _, progress, _ -> mediaPresenter.seekChanging(progress) }
        }

        btn_cover.setOnClickListener(onBtnCoverClicked)
        btn_save.setOnClickListener(onBtnSaveClicked)
        btn_restore.setOnClickListener(onBtnRestoreClicked)
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
            showAlert("Failed", "File not found")
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
