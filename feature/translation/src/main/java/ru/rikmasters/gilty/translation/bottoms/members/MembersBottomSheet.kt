package ru.rikmasters.gilty.translation.bottoms.members

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import ru.rikmasters.gilty.shared.R
import ru.rikmasters.gilty.shared.common.extentions.simpleVerticalScrollbar
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.components.MemberItem
import ru.rikmasters.gilty.translation.shared.components.SearchBar

@Composable
fun MembersBottomSheet(
    configuration: Configuration,
    membersCount: Int,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    membersList: LazyPagingItems<FullUserModel>?,
    onComplainClicked: (FullUserModel) -> Unit,
    onDeleteClicked: (FullUserModel) -> Unit
) {
    val scrollState = rememberLazyListState()
    Box(
        modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Modifier
                .width((configuration.screenWidthDp * 0.4).dp)
                .fillMaxHeight()
                .background(
                    color = ThemeExtra.colors.blackSeventy,
                    shape = RoundedCornerShape(
                        topStart = 24.dp
                    )
                )
                .padding(horizontal = 16.dp)
        } else if (membersCount == 0) {
            Modifier
                .fillMaxWidth()
                .height((configuration.screenHeightDp * 0.375).dp)
                //.wrapContentWidth(unbounded = false)
                .background(
                    color = ThemeExtra.colors.blackSeventy
                )
                .padding(horizontal = 16.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .height((configuration.screenHeightDp * 0.75).dp)
                //.wrapContentWidth(unbounded = false)
                .background(
                    color = ThemeExtra.colors.blackSeventy
                )
                .padding(horizontal = 16.dp)
        }
    ) {
        Column(
            modifier = Modifier.matchParentSize()
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .height(5.dp)
                    .width(40.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(11.dp),
                color = ThemeExtra.colors.bottomSheetGray
            ) {}
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier.simpleVerticalScrollbar(
                    state = scrollState,
                    width = 3.dp
                )
            ) {
                if (membersCount == 0 && membersList?.loadState?.refresh is LoadState.NotLoading) {
                    Text(
                        text = stringResource(id = R.string.translations_members),
                        style = ThemeExtra.typography.TranslationTitlePreview,
                        color = ThemeExtra.colors.white,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.broken_heart),
                            contentDescription = "",
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = stringResource(id = R.string.translations_members_no_users),
                            color = ThemeExtra.colors.bottomSheetGray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 15.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.translations_members),
                            style = ThemeExtra.typography.TranslationTitlePreview,
                            color = ThemeExtra.colors.white
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (membersCount > 0) {
                            Text(
                                text = if (membersCount >= 1000) {
                                    if (membersCount >= 1000000) {
                                        "${membersCount/1000000}КK"
                                    } else {
                                        "${membersCount/1000}К"
                                    }
                                } else {
                                    membersCount.toString()
                                },
                                style = ThemeExtra.typography.TranslationTitlePreview,
                                color = ThemeExtra.colors.mainNightGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    SearchBar(
                        onSearchValueChanged = onSearchValueChange,
                        searchValue = searchValue
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    LazyColumn(
                        state = scrollState
                    ) {
                        membersList?.let {
                            items(membersList) { fullUserModel ->
                                fullUserModel?.let {
                                    MemberItem(
                                        user = it,
                                        onComplainClicked = { onComplainClicked(it) },
                                        onDeleteClicked = { onDeleteClicked(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}