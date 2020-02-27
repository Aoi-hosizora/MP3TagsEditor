@file:Suppress("DEPRECATION")

package com.aoihosizora.mp3tagseditor.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.EditText
import android.widget.Toast

interface IContextHelper {

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.showAlert(
        title: CharSequence, message: CharSequence,
        posText: CharSequence = "OK", posListener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posText, posListener)
            .show()
    }

    fun Context.showAlert(
        title: CharSequence, message: CharSequence,
        posText: CharSequence, posListener: ((DialogInterface, Int) -> Unit)? = null,
        negText: CharSequence, negListener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posText, posListener)
            .setNegativeButton(negText, negListener)
            .show()
    }

    fun Context.showAlert(
        title: CharSequence, view: View,
        posText: CharSequence, posListener: ((DialogInterface, Int) -> Unit)? = null,
        negText: CharSequence, negListener: ((DialogInterface, Int) -> Unit)? = null,
        netText: CharSequence, netListener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(posText, posListener)
            .setNegativeButton(negText, negListener)
            .setNeutralButton(netText, netListener)
            .show()
    }

    fun Context.showAlert(
        title: CharSequence, view: View,
        posText: CharSequence, posListener: ((DialogInterface, Int) -> Unit)? = null,
        negText: CharSequence, negListener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setPositiveButton(posText, posListener)
            .setNegativeButton(negText, negListener)
            .show()
    }

    fun Context.showAlert(
        title: CharSequence,
        list: Array<out CharSequence>,
        listener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(list, listener)
            .show()
    }

    fun Context.showInputDlg(
        title: CharSequence, text: CharSequence = "", hint: CharSequence = "", maxLines: Int = 5,
        posText: CharSequence, posClick: (DialogInterface, Int, String) -> Unit,
        negText: CharSequence, negListener: ((DialogInterface, Int) -> Unit)? = null
    ) {
        val edt = EditText(this)
        edt.hint = hint
        edt.setText(text)
        edt.setSingleLine(true)
        edt.maxLines = maxLines
        edt.setHorizontallyScrolling(false)

        showAlert(
            title = title, view = edt,
            posText = posText,
            posListener = { dialogInterface, i -> posClick(dialogInterface, i, edt.text.toString()) },
            negText = negText, negListener = negListener
        )
    }

    fun Context.showProgress(
        context: Context, message: CharSequence,
        cancelable: Boolean = true, onCancelListener: ((DialogInterface) -> Unit)? = null
    ): ProgressDialog {
        val progressDlg = ProgressDialog(context)
        progressDlg.setMessage(message)
        progressDlg.setCancelable(cancelable)
        progressDlg.setOnCancelListener(onCancelListener)
        progressDlg.show()
        return progressDlg
    }

    fun Context.showSnackBar(message: CharSequence, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun Context.showSnackBar(
        message: CharSequence, view: View,
        action: CharSequence, listener: ((View) -> Unit)? = null
    ) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).setAction(action, listener).show()
    }
}
