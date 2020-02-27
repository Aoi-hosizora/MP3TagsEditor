package com.aoihosizora.mp3tagseditor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.aoihosizora.mp3tagseditor.R
import kotlinx.android.synthetic.main.activity_main.*
import rx_activity_result2.RxActivityResult
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
    }

    private val onBtnSaveClicked: (View) -> Unit = {
        showToast("save")
    }

    private fun loadMusic(uri: Uri) {
        txt_file.text = String.format("Opening %s", uri.path)
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
