package com.aoihosizora.mp3tagseditor.util

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore.Images
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File


object ImageUtil {

    fun getBitmapFromUri(resolver: ContentResolver, uri: Uri): Bitmap? {
        return Images.Media.getBitmap(resolver, uri)
    }

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

    fun saveImage(context: Context, contentResolver: ContentResolver, bm: Bitmap, title: String, description: String) {
        val inserted = Images.Media.insertImage(contentResolver, bm, title, description)
        val f = File(PathUtil.getFilePathByUri(context, Uri.parse(inserted)))

        val intent = Intent()
        intent.action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
        intent.data = Uri.fromFile(f)
        context.sendBroadcast(intent)
    }
}
