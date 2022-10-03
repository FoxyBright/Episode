package ru.rikmasters.gilty.presentation.ui.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

class Category(val name: String, val color: Color)

@Composable
fun setArray(): ArrayList<List<Category>> {
    val category = arrayListOf<Category>()
    repeat(20) { i -> category.add(Category("Cinema $i", ThemeExtra.colors.primary)) }
    val chunkedList = category.chunked(3)
    val first = arrayListOf<Category>()
    val second = arrayListOf<Category>()
    val third = arrayListOf<Category>()
    for (list in chunkedList) when {
        chunkedList.indexOf(list) % 3 == 0 -> for (element in list) first.add(element)
        chunkedList.indexOf(list) % 2 == 0 -> for (element in list) second.add(element)
        else -> for (element in list) third.add(element)
    }
    val categoryRowList = arrayListOf<List<Category>>()
    categoryRowList.add(first)
    categoryRowList.add(second)
    categoryRowList.add(third)
    return categoryRowList
}

@Composable
fun TextContent() {
    val list = setArray()
    Column(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        CategoryRow(list[0])
        CategoryRow(list[1])
        CategoryRow(list[2])
    }
}

@Composable
private fun CategoryRow(items: List<Category>, reflectedWave: Boolean = false) {
    if (reflectedWave)
        Row {
            for (item in items) {
                val align = if (items.indexOf(item) % 2 != 0) {
                    Alignment.TopCenter
                } else {
                    Alignment.BottomCenter
                }
                CategoryItem(align, item.name, item.color)
            }
        }
    else
        Row {
            for (item in items) {
                val align = if (items.indexOf(item) % 2 == 0) {
                    Alignment.TopCenter
                } else {
                    Alignment.BottomCenter
                }
                CategoryItem(align, item.name, item.color)
            }
        }
}

@Composable
private fun CategoryItem(position: Alignment, text: String, color: Color) {
    Box(
        Modifier
            .width(150.dp)
            .height(170.dp), position
    ) {
        Box {
            Box(
                Modifier
                    .width(140.dp)
                    .height(140.dp)
                    .clip(CircleShape)
                    .background(color)
                    .align(Alignment.BottomCenter), Alignment.Center
            ) {
                Text(
                    text, Modifier, ThemeExtra.colors.white, 14.sp, fontWeight = FontWeight.Bold
                )
            }
            Box(
                Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(ThemeExtra.colors.white)
                    .align(Alignment.BottomStart),
                Alignment.Center
            ) {
                Image(painterResource(R.drawable.cinema), "", Modifier.size(22.dp))
            }
        }
    }
}
