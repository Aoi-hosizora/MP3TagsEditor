@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PrivatePropertyName")

package com.aoihosizora.mp3tagseditor.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.aoihosizora.mp3tagseditor.R

class ResultView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val progress: ProgressBar
    private val iv_success: ImageView
    private val iv_failed: ImageView
    private val iv_cancel: ImageView
    private val txt_message: TextView

    enum class State {
        Init, Running, Success, Failed, Cancel
    }

    private var state: State

    private var _message: String = ""
    private var _runningText: String = "Running"
    private var _successText: String = "Success"
    private var _failedText: String = "Failed"
    private var _cancelText: String = "Cancel"

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_result, this, true)

        progress = findViewById(R.id.progress)
        iv_success = findViewById(R.id.iv_success)
        iv_failed = findViewById(R.id.iv_failed)
        iv_cancel = findViewById(R.id.iv_cancel)
        txt_message = findViewById(R.id.txt_message)

        attrs?.let { a ->
            val typedArray = context.obtainStyledAttributes(a, R.styleable.ResultView)
            typedArray.getString(R.styleable.ResultView_message)?.let { message = it }
            typedArray.getString(R.styleable.ResultView_runningText)?.let { runningText = it }
            typedArray.getString(R.styleable.ResultView_successText)?.let { successText = it }
            typedArray.getString(R.styleable.ResultView_failedText)?.let { failedText = it }
            typedArray.getString(R.styleable.ResultView_cancelText)?.let { cancelText = it }
            typedArray.recycle()
        }

        state = State.Init

        init()
    }

    var message: String
        get() = _message
        set(value) {
            _message = value
            update()
        }

    var runningText: String
        get() = _runningText
        set(value) {
            _runningText = value
            update()
        }

    var successText: String
        get() = _successText
        set(value) {
            _successText = value
            update()
        }

    var failedText: String
        get() = _failedText
        set(value) {
            _failedText = value
            update()
        }

    var cancelText: String
        get() = _cancelText
        set(value) {
            _cancelText = value
            update()
        }

    fun init() {
        state = State.Init
        update()
        progress.visibility = View.GONE
        iv_success.visibility = View.GONE
        iv_failed.visibility = View.GONE
        iv_cancel.visibility = View.GONE
    }

    fun startRun() {
        state = State.Running
        update()
        progress.visibility = View.VISIBLE
        iv_success.visibility = View.GONE
        iv_failed.visibility = View.GONE
        iv_cancel.visibility = View.GONE
    }

    fun success() {
        state = State.Success
        update()
        progress.visibility = View.GONE
        iv_success.visibility = View.VISIBLE
        iv_failed.visibility = View.GONE
        iv_cancel.visibility = View.GONE
    }

    fun failed() {
        state = State.Failed
        update()
        progress.visibility = View.GONE
        iv_success.visibility = View.GONE
        iv_failed.visibility = View.VISIBLE
        iv_cancel.visibility = View.GONE
    }

    fun cancel() {
        state = State.Cancel
        update()
        progress.visibility = View.GONE
        iv_success.visibility = View.GONE
        iv_failed.visibility = View.GONE
        iv_cancel.visibility = View.VISIBLE
    }

    private fun update() {
        Log.i("", state.toString())
        txt_message.text = when (state) {
            State.Running -> runningText
            State.Success -> successText
            State.Failed -> failedText
            State.Cancel -> cancelText
            else -> message
        }
    }
}
