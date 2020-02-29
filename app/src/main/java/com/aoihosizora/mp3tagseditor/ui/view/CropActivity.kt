package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.aoihosizora.mp3tagseditor.R
import com.aoihosizora.mp3tagseditor.ui.IContextHelper
import com.aoihosizora.mp3tagseditor.util.CoverUtil
import kotlinx.android.synthetic.main.activity_crop.*


class CropActivity : AppCompatActivity(), IContextHelper {

    companion object {
        const val INTENT_BITMAP = "INTENT_BITMAP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        supportActionBar?.hide()
        toolbar.inflateMenu(R.menu.activity_crop_menu)

        initView()
    }

    private fun initView() {
        val bm = CoverUtil.getImageFromExtra(intent, INTENT_BITMAP)
        if (bm == null) {
            finish()
        }
        iv_crop.setImageBitmap(bm!!)
        toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_crop -> onMenuCropSelected()
        }
        return true
    }

    private val onMenuCropSelected: () -> Unit = {
        val intent = Intent()
        CoverUtil.putImageToExtra(intent, INTENT_BITMAP, iv_crop.croppedImage)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
