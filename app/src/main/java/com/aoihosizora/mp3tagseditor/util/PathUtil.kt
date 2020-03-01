package com.aoihosizora.mp3tagseditor.util

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File
import java.lang.Exception

object PathUtil {

    fun getFilenameWithoutExt(filename: String): String {
        val idx = filename.indexOf(".")
        return if (idx == -1) filename else filename.substring(0, idx)
    }

    fun getFilePathByUri(context: Context, uri: Uri): String {
        if (uri.path == null) {
            return ""
        }

        // 1. file://
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            return uri.path ?: ""
        }

        // 2. content://media/external/audio/media/222
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val path = getDataColumn(context, uri)
            if (path.isNotBlank()) {
                return path
            }
        }

        // 3. content://com.amaze.filemanager/storage_root/storage/emulated/0/xxx
        // 3. content://com.android.providers.media.documents/document/image%3A235700
        if (uri.scheme == ContentResolver.SCHEME_CONTENT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) { // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri)
            } else if (isAmazeFileManagerDocument(uri)) { // AmazeFileManager
                val path = uri.path ?: return ""
                return path.substring("/storage_root".length)
            } else if (isMediaDocument(uri)) { // MediaProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> return ""
                }
                return getDataColumn(context, contentUri, "_id=?", arrayOf(split[1]))
            }
        }
        return ""
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String? = null, selectionArgs: Array<String>? = null): String {
        val column = "_data" // MediaStore.Audio.Media.DATA
        val cursor = context.contentResolver.query(uri, arrayOf(column), selection, selectionArgs, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return try {
                    val idx = cursor.getColumnIndexOrThrow(column)
                    cursor.getString(idx)
                } catch (ex: Exception) {
                    ""
                }
            }
            cursor.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return uri.authority == "com.android.externalstorage.documents"
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return uri.authority == "com.android.providers.downloads.documents"
    }

    private fun isAmazeFileManagerDocument(uri: Uri): Boolean {
        return uri.authority == "com.amaze.filemanager"
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return uri.authority == "com.android.providers.media.documents"
    }
}
