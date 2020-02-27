package com.aoihosizora.mp3tagseditor.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aoihosizora.mp3tagseditor.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IContextHelper {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView(hasFile: Boolean = false) {
        val def = if (hasFile) View.VISIBLE else View.GONE
        val vis = if (!hasFile) View.VISIBLE else View.GONE

        txt_open.visibility = vis
        btn_open.visibility = vis
        txt_file.visibility = def
        btn_close.visibility = def
        view_divide_head.visibility = def
        ll_main.visibility = def
        view_divide_bottom.visibility = def
        ll_bottom.visibility = def

        btn_open.setOnClickListener(onBtnOpenClicked)
        btn_close.setOnClickListener(onBtnCloseClicked)
        btn_save.setOnClickListener(onBtnSaveClicked)
    }

    private val onBtnOpenClicked: (View) -> Unit = {
        initView(true)
    }

    private val onBtnCloseClicked: (View) -> Unit = {
        initView(false)
    }

    private val onBtnSaveClicked: (View) -> Unit = {
        showToast("save")
    }
}
