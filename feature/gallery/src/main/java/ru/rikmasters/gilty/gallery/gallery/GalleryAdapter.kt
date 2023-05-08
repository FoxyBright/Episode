package ru.rikmasters.gilty.gallery.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment.MEDIA_MOUNTED
import android.os.Environment.getExternalStorageState
import android.provider.MediaStore.Images.Media.*
import android.provider.MediaStore.MediaColumns.DATA

class GalleryAdapter {
    
    companion object {
        
        @SuppressLint("Range")
        fun getImages(
            context: Context,
            filter: String? = null,
        ): MutableList<String> {
            // creating variable
            val imgList: MutableList<String> = ArrayList()
            // check permissions
            (if(getExternalStorageState() == MEDIA_MOUNTED) {
                // get cursor
                onCreateLoader(context)?.let { cursor ->
                    // iterating through the list
                    repeat(cursor.count - 1) { index ->
                        try {
                            // moving the cursor
                            cursor.moveToPosition(index + 1)
                            // get file path
                            imgList.add(
                                cursor.getString(
                                    cursor.getColumnIndex(DATA)
                                )
                            )
                            // filter image list
                            filter?.let {
                                imgList.filter {
                                    it.contains("/$filter")
                                }
                            }
                        } catch(e: Exception) {
                            e.stackTraceToString()
                        }
                    }
                    cursor.close()
                }
            } else emptyList<String>())
            // result
            return imgList
        }
        
        private fun onCreateLoader(
            context: Context,
            uri: Uri = EXTERNAL_CONTENT_URI,
            protection: Array<String> = arrayOf(DATA, BUCKET_DISPLAY_NAME),
            selection: String? = null,
            selectionArgs: Array<String>? = null,
            sortOrder: String = DATE_MODIFIED,
        ) = context.contentResolver.query(
            uri, protection, selection, selectionArgs, sortOrder
        )
    }
}