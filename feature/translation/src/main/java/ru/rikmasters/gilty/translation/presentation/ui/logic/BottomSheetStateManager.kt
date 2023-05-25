package ru.rikmasters.gilty.translation.presentation.ui.logic

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.translation.model.BottomSheetState
import ru.rikmasters.gilty.translation.presentation.ui.content.bottomsheet.ChatBottomSheet
import ru.rikmasters.gilty.translation.presentation.ui.content.bottomsheet.ExtendBottomSheet
import ru.rikmasters.gilty.translation.presentation.ui.content.bottomsheet.MembersBottomSheet

@Composable
fun BottomSheetStateManager(
    modifier: Modifier,
    state: BottomSheetState,
    configuration: Configuration,
    messagesList: LazyPagingItems<TranslationMessageModel>?,
    onSendMessage: (String) -> Unit,
    membersCount: Int,
    searchValue: String,
    onSearchValueChange: (String) -> Unit,
    membersList: LazyPagingItems<FullUserModel>?,
    onComplainClicked: (FullUserModel) -> Unit,
    onDeleteClicked: (FullUserModel) -> Unit,
    onAppendDurationSave: (Int) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        when(state) {
            BottomSheetState.CHAT -> {
                ChatBottomSheet(
                    configuration = configuration,
                    messagesList = messagesList,
                    onSendMessage = onSendMessage
                )
            }
            BottomSheetState.USERS -> {
                MembersBottomSheet(
                    configuration = configuration,
                    membersCount = membersCount,
                    searchValue = searchValue,
                    onSearchValueChange = onSearchValueChange,
                    membersList = membersList,
                    onComplainClicked = onComplainClicked,
                    onDeleteClicked = onDeleteClicked
                )
            }
            BottomSheetState.DURATION -> {
                ExtendBottomSheet(
                    onSave = onAppendDurationSave
                )
            }
        }
    }
}