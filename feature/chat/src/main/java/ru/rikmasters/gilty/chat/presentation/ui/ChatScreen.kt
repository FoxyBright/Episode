package ru.rikmasters.gilty.chat.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModel
import ru.rikmasters.gilty.chat.presentation.model.DemoMessageModelWithAnswer
import ru.rikmasters.gilty.core.app.AppStateModel
import ru.rikmasters.gilty.shared.model.meeting.DemoMemberModel
import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val scope = rememberCoroutineScope()
    val asm = get<AppStateModel>()
    val chatAppBarState = ChatAppBarState(
        "Бэтмен", DemoAvatarModel, 2
    )
    var messageText by remember { mutableStateOf("") }
    val answer by remember {
        mutableStateOf<MessageAnswerState?>(null)
    }
    Scaffold(
        Modifier, { ChatAppBarContent(chatAppBarState) },
        {
            MessengerBar(messageText, Modifier, answer, object : MessengerBarCallback {
                override fun textChange(text: String) {
                    messageText = text
                }

                override fun gallery() {
                    scope.launch {
                        asm.bottomSheetState.expand {
                            Text("Скрытые фотки", Modifier.height(300.dp))
                        }
                    }
                }
            })
        }
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(Modifier.align(Alignment.BottomCenter)) {
                item { MessageContent(DemoMessageModel, true) }
                item {
                    MessageContent(
                        DemoMessageModelWithAnswer,
                        (false), Pair(
                            DemoMessageModel,
                            DemoMemberModel
                        )
                    )
                }
                item { MessageContent(DemoMessageModel, true) }
                item { MessageContent(DemoMessageModel, false) }
                item { Divider(Modifier, 10.dp, Color.Transparent) }
            }
        }
    }
}