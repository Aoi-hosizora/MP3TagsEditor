package com.aoihosizora.mp3tagseditor.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
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
        toolbar.inflateMenu(R.menu.activity_crop_menu)

        initView()
    }

    private fun initView() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        val bm = CoverUtil.getImageFromExtra(intent, INTENT_BITMAP)
        if (bm == null) {
            finish()
        }
        iv_crop.setImageBitmap(bm!!)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.setOnMenuItemClickListener { onMenuItemClickListener(it) }
    }

    private fun onMenuItemClickListener(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_crop -> {
                val intent = Intent()
                CoverUtil.putImageToExtra(intent, INTENT_BITMAP, iv_crop.croppedImage)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return true
    }
}
