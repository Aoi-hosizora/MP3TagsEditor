package com.aoihosizora.mp3tagseditor.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aoihosizora.mp3tagseditor.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    fun initView() {
        view_divider.visibility = View.GONE
        ll_second.visibility = View.GONE
    }
}
