package ru.rikmasters.gilty.presentation.ui.presentation.login

import android.content.res.Resources
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.R
import ru.rikmasters.gilty.presentation.model.profile.Country
import ru.rikmasters.gilty.presentation.model.profile.CountryList
import ru.rikmasters.gilty.presentation.ui.presentation.profile.AgeBottomSheetComposeCallback
import ru.rikmasters.gilty.presentation.ui.shared.Divider
import ru.rikmasters.gilty.presentation.ui.shared.DividerBold
import ru.rikmasters.gilty.presentation.ui.shared.TransparentTextFieldColors
import ru.rikmasters.gilty.presentation.ui.theme.base.GiltyTheme
import ru.rikmasters.gilty.presentation.ui.theme.base.ThemeExtra

interface CountryBottomSheetComposeCallback: AgeBottomSheetComposeCallback {
    fun onCountryClick(value: Country)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryBottomSheetCompose(
    modifier: Modifier = Modifier,
    state: Boolean = true,
    callback: CountryBottomSheetComposeCallback? = null
) {
    val sizeTransition = updateTransition(
        remember {
            MutableTransitionState(state).apply {
                targetState = !state
            }
        }, ""
    ).animateDp(
        { tween(durationMillis = 500) },
        "",
        { if (state) 600.dp else 0.dp },
    ).value
    val searchState = remember { mutableStateOf(true) }
    val searchTransaction = updateTransition(
        remember {
            MutableTransitionState(searchState.value).apply {
                targetState = !searchState.value
            }
        }, ""
    ).animateDp(
        { tween(durationMillis = 200) },
        "",
        { if (searchState.value) Resources.getSystem().displayMetrics.widthPixels.dp else 0.dp },
    ).value
    Column(
        modifier
            .fillMaxWidth()
            .height(sizeTransition)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < 6) callback?.onDownDrag()
                }
            }
    ) {
        Box(Modifier.fillMaxWidth()) {
            DividerBold(
                Modifier
                    .width(40.dp)
                    .align(Alignment.TopCenter)
                    .padding(vertical = 10.dp)
                    .clip(CircleShape)
            )
        }
        Column(
            modifier
                .height(600.dp)
                .fillMaxWidth()
                .background(ThemeExtra.colors.cardBackground)
        ) {
            val text = remember { mutableStateOf("") }
            val countryList = remember { mutableStateOf(CountryList) }

            Box(Modifier.fillMaxWidth()) {
                Box(
                    Modifier
                        .width(searchTransaction)
                        .height(70.dp)
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(ThemeExtra.colors.searchCardBackground)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(ThemeExtra.colors.searchCardBackground),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton({ searchState.value = false }) {
                            Icon(
                                painterResource(
                                    R.drawable.ic_back
                                ),
                                stringResource(R.string.action_bar_button_back),
                                Modifier.size(20.dp),
                                ThemeExtra.colors.mainTextColor
                            )
                        }
                        TextField(
                            text.value,
                            { text.value = it },
                            Modifier
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = TransparentTextFieldColors(),
                            placeholder = {
                                Text(
                                    stringResource(R.string.login_search_placeholder),
                                    color = ThemeExtra.colors.secondaryTextColor,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            singleLine = true,
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(!searchState.value) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            "Страна и регион",
                            Modifier.padding(start = 16.dp),
                            style = ThemeExtra.typography.H3
                        )
                        IconButton({ searchState.value = true }, Modifier.padding(end = 16.dp)) {
                            Icon(
                                painterResource(
                                    R.drawable.magnifier
                                ),
                                stringResource(R.string.login_search_placeholder),
                                Modifier.size(22.dp),
                                ThemeExtra.colors.mainTextColor
                            )
                        }
                    }
                }
            }
            LazyColumn(
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(ThemeExtra.colors.searchCardBackground)
            ) {
                itemsIndexed(countryList.value) { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { callback?.onCountryClick(item) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painterResource(item.flag),
                            item.country,
                            Modifier.size(24.dp)
                        )
                        Text(item.country, Modifier.padding(start = 16.dp))
                    }
                    if (index < CountryList.size - 1) Divider(Modifier.padding(start = 54.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AgeBottomSheetPreview() {
    GiltyTheme {
        CountryBottomSheetCompose(state = true)
    }
}