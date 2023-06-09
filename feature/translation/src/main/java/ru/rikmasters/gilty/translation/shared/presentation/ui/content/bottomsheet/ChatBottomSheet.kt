package ru.rikmasters.gilty.translation.shared.presentation.ui.content.bottomsheet

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.shared.theme.base.ThemeExtra
import ru.rikmasters.gilty.translation.shared.presentation.ui.components.CommentPanel
import ru.rikmasters.gilty.translation.shared.presentation.ui.components.MessageItem

@Composable
fun ChatBottomSheet(
    configuration: Configuration,
    messagesList: LazyPagingItems<TranslationMessageModel>?,
    onSendMessage: (String) -> Unit,
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
        } else {
            Modifier
                .fillMaxWidth()
                .height((configuration.screenHeightDp * 0.375).dp)
                .wrapContentWidth(unbounded = false)
                .background(
                    color = ThemeExtra.colors.blackSeventy
                )
                .padding(horizontal = 16.dp)
        },
        contentAlignment = Alignment.CenterEnd
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
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier.simpleVerticalScrollbar(
                    state = scrollState,
                    width = 3.dp
                )
            ) {
                if (messagesList?.itemCount == 0 && messagesList.loadState.refresh is LoadState.NotLoading) {
                    Text(
                        text = stringResource(id = R.string.translations_chat),
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
                        Text(
                            text = stringResource(id = R.string.translations_members_no_messages),
                            color = ThemeExtra.colors.bottomSheetGray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                } else {
                    LazyColumn(
                        state = scrollState,
                        reverseLayout = true
                    ) {
                        messagesList?.let {
                            items(messagesList) { messageModel ->
                                messageModel?.let {
                                    MessageItem(
                                        messageModel = it
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
        CommentPanel(
            onSendMessage = onSendMessage,
            modifier = Modifier
                .align(
                    Alignment.BottomCenter
                )
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
    }
}