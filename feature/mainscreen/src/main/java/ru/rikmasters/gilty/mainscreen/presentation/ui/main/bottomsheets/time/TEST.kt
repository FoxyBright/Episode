package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets.time

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview
@Composable
fun Preview() {
    val list = arrayListOf<String>()
    repeat(24) {
        list.add(it.toString())
    }
    FeatureList(
        list,
        Modifier
            .fillMaxWidth()
            .height(110.dp)
    )
}


@Composable
fun <T> Element(value: T, active: Boolean) {
    Card(
        Modifier
            .size(30.dp)
            .aspectRatio(1f)
            .padding(1.dp),
        colors = cardColors(Transparent)
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                value.toString(),
                Modifier.background(
                    if(active) Color.Magenta
                    else Color.Cyan
                ),
                style = typography.titleLarge
            )
        }
    }
}

@Composable
@SuppressLint("CoroutineCreationDuringComposition")
fun <T> FeatureList(
    list: List<T>,
    modifier: Modifier,
) {
    var itemsListState by remember { mutableStateOf(list) }
    val listState = rememberLazyListState()
    
    val scope = rememberCoroutineScope()
    val firstItem = remember { derivedStateOf { listState.firstVisibleItemIndex } }.value
    val offset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }.value
    
    LazyColumn(
        modifier, listState,
    ) {
        itemsIndexed(itemsListState) { index, it ->
            Element(it, (index == firstItem + 1))
            Spacer(modifier = Modifier.width(6.dp))
            
            if(!listState.isScrollInProgress) {
                if(offset <= 250) scope.scrollBasic(listState, (true))
                else scope.scrollBasic(listState)
                if(offset > 250) scope.scrollBasic(listState)
                else scope.scrollBasic(listState, (true))
            }
            
            if(it == itemsListState.last()) {
                val currentList = itemsListState
                val secondPart = currentList.subList(0, firstItem)
                val firstPart = currentList.subList(firstItem, currentList.size)
                rememberCoroutineScope().launch {
                    listState.scrollToItem((0), maxOf((0), (offset - 1)))
                }
                itemsListState = firstPart + secondPart
            }
        }
    }
}

private fun CoroutineScope.scrollBasic(
    listState: LazyListState,
    left: Boolean = false,
) {
    launch {
        listState.animateScrollToItem(
            if(left) listState.firstVisibleItemIndex
            else listState.firstVisibleItemIndex + 1
        )
    }
}