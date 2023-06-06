package ru.rikmasters.gilty.translation.shared.logic

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import ru.rikmasters.gilty.shared.model.meeting.FullUserModel
import ru.rikmasters.gilty.shared.model.translations.TranslationMessageModel
import ru.rikmasters.gilty.translation.shared.model.TranslationBottomSheetState
import ru.rikmasters.gilty.translation.shared.presentation.ui.content.bottomsheet.ChatBottomSheet
import ru.rikmasters.gilty.translation.shared.presentation.ui.content.bottomsheet.ExtendBottomSheet
import ru.rikmasters.gilty.translation.shared.presentation.ui.content.bottomsheet.MembersBottomSheet

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
    onAppendDurationSave: (Int) -> Unit
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
                    onDeleteClicked = onDeleteClicked
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