package ru.rikmasters.gilty.translation.bottoms

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.translation.bottoms.chat.ChatBottomSheet
import ru.rikmasters.gilty.translation.bottoms.duration.ExtendBottomSheet
import ru.rikmasters.gilty.translation.bottoms.members.MembersBottomSheet

@Composable
fun BottomSheetStateManager(
    modifier: Modifier,
    state: TranslationBottomSheetState,
    configuration: Configuration,
    messagesList: LazyPagingItems<TranslationMessageModel>?,
    onSendMessage: (String) -> Unit,
    membersCount: Int,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    membersList: LazyPagingItems<FullUserModel>?,
    onComplainClicked: (FullUserModel) -> Unit,
    onDeleteClicked: (FullUserModel) -> Unit,
    onAppendDurationSave: (Int) -> Unit,
    isOrganizer: Boolean
) {
    Box(
        modifier = modifier
    ) {
        when(state) {
            TranslationBottomSheetState.CHAT -> {
                ChatBottomSheet(
                    configuration = configuration,
                    messagesList = messagesList,
                    onSendMessage = onSendMessage
                )
            }
            TranslationBottomSheetState.USERS -> {
                MembersBottomSheet(
                    configuration = configuration,
                    membersCount = membersCount,
                    searchValue = searchValue,
                    onSearchValueChange = onSearchValueChange,
                    membersList = membersList,
                    onComplainClicked = onComplainClicked,
                    onDeleteClicked = onDeleteClicked,
                    isOrganizer = isOrganizer
                )
            }
            TranslationBottomSheetState.DURATION -> {
                ExtendBottomSheet(
                    onSave = onAppendDurationSave,
                    configuration = configuration
                )
            }
        }
    }
}