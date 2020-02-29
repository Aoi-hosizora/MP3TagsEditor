package com.aoihosizora.mp3tagseditor.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.ByteArrayOutputStream

object CoverUtil {

    fun getBitmapFromByteArray(arr: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(arr, 0, arr.size)
    }

    fun getBitmapFromImageView(im: ImageView): Bitmap {
        return (im.drawable as BitmapDrawable).bitmap
    }

    fun getJpegByteArrayFromBitmap(bm: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun putImageToExtra(intent: Intent, name: String, bm: Bitmap) {
        intent.putExtra(name, getJpegByteArrayFromBitmap(bm))
    }

    fun getImageFromExtra(intent: Intent, name: String): Bitmap? {
        val arr = intent.getByteArrayExtra(name)
        return getBitmapFromByteArray(arr)
    }
}
