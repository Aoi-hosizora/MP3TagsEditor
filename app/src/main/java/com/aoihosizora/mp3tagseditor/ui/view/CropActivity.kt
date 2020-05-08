@file:Suppress("SetTextI18n")

package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.aoihosizora.mp3tagseditor.MyApplication
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.ui.contract.CropActivityContract
import com.aoihosizora.mp3tagseditor.ui.presenter.CropActivityPresenter
import kotlinx.android.synthetic.main.activity_crop.*


class CropActivity : AppCompatActivity(), IContextHelper, CropActivityContract.View {

    override val presenter = CropActivityPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        supportActionBar?.hide()
        toolbar.inflateMenu(R.menu.activity_crop_menu)

        initView()
    }

    private fun initView() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        val bm = (application as MyApplication).getObject() as? Bitmap
        if (bm == null) {
            showAlert("Failed", "Couldn't load cover.")
            finish()
        }
        iv_crop.setImageBitmap(bm!!)
        updateScaleSize()
        iv_crop.setOnSetCropOverlayMovedListener { updateScaleSize() }

        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener { onMenuItemClickListener(it) }
    }

    private fun onMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_crop -> {
                val intent = Intent()
                (application as MyApplication).setObject(iv_crop.croppedImage)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return true
    }

    /**
     * update title size
     */
    private fun updateScaleSize() {
        txt_scale_size.text = "Current size: ${iv_crop.cropWindowRect.width().toInt()} x ${iv_crop.cropWindowRect.height().toInt()}"
    }
}
