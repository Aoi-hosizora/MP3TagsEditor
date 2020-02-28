package com.aoihosizora.mp3tagseditor.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.nio.ByteBuffer


object CoverUtil {

    fun getBitmapFromByteArray(arr: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(arr, 0, arr.size)
    }

    fun getBitmapFromImageView(im: ImageView): Bitmap {
        return (im.drawable as BitmapDrawable).bitmap
    }

    fun getByteArrayFromBitmap(bm: Bitmap): ByteArray {
        val bytes = bm.byteCount
        val buf = ByteBuffer.allocate(bytes)
        bm.copyPixelsToBuffer(buf)
        return buf.array()
    }
}