package ru.rikmasters.gilty.gallery.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment.MEDIA_MOUNTED
import android.os.Environment.getExternalStorageState
import android.provider.MediaStore.Images.Media.*
import android.provider.MediaStore.MediaColumns.DATA

object GalleryAdapter {
    
    private val uri = EXTERNAL_CONTENT_URI
    private val protection = arrayOf(
        DATA, "bucket_display_name"
    )
    
    private fun Context.onCreateLoader() = contentResolver
        .query(uri, protection, (null), (null), DATE_MODIFIED)
    
    @SuppressLint("Range")
    fun getImages(
        context: Context,
        filter: String = "",
    ): MutableList<String> {
        val imgList = arrayListOf<String>()
        
        val permission =
            getExternalStorageState() == MEDIA_MOUNTED
        
        if(permission) context
            .onCreateLoader()
            ?.let { cursor ->
                repeat(cursor.count - 1) { i ->
                    cursor.moveToPosition((i + 1))
                    cursor.getString(
                        cursor.getColumnIndex(DATA)
                    ).let {
                        if(it.contains("/$filter"))
                            imgList.add(it)
                    }
                }
                
                cursor.close()
            }
        
        return imgList
    }
}