package ru.rikmasters.gilty.shared.common.extentions

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

fun <T: Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(item: T?) -> Unit,
) {
    items(
        items.itemCount, key?.let { k ->
            { index ->
                items.peek(index)
                    ?.let { k(it) }
                    ?: PagingPlaceholderKey(
                        index
                    )
            }
        }
    ) { itemContent(items[it]) }
}

@SuppressLint("BanParcelableUsage")
private data class PagingPlaceholderKey(
    private val index: Int,
): Parcelable {
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object {
        
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
            object: Parcelable.Creator<PagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey(parcel.readInt())
                
                override fun newArray(size: Int) =
                    arrayOfNulls<PagingPlaceholderKey?>(size)
            }
    }
}