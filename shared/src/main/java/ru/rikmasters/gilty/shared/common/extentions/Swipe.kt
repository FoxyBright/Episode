//package ru.rikmasters.gilty.shared.common.extentions
//
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material3.*
//import androidx.compose.material3.CardDefaults.cardElevation
//import androidx.compose.material3.MaterialTheme.colorScheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import ru.rikmasters.gilty.shared.model.meeting.MemberModel
//import ru.rikmasters.gilty.shared.model.profile.DemoAvatarModel
//import ru.rikmasters.gilty.shared.model.profile.DemoEmojiModel
//
//
//@Preview
//@Composable
//@OptIn(ExperimentalMaterialApi::class)
//fun Swipe() {
//
//    val messageList = remember {
//        mutableStateListOf(
//            MemberModel(("0"), ("cristina 0"), DemoEmojiModel, DemoAvatarModel, (23)),
//        )
//    }
//    repeat(20) {
//        messageList.add(
//            MemberModel(
//                ("${it + 1}"), ("cristina ${it + 1}"),
//                DemoEmojiModel, DemoAvatarModel, (23)
//            )
//        )
//    }
//
//    LazyColumn(Modifier.fillMaxSize()) {
//        items(messageList, { it.id }) { item ->
//            val dismissState = rememberDismissState(
//                confirmStateChange = {
//                    if(it == DismissValue.DismissedToEnd
//                        || it == DismissValue.DismissedToStart
//                    ) {
//                        messageList.remove(item)
//                    }
//                    true
//                })
//
//            SwipeToDismiss(
//                dismissState, Modifier,
//                setOf(DismissDirection.StartToEnd, DismissDirection.StartToEnd),
//                background = {
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .background(colorScheme.primary))
//                },
//                dismissContent = {
//                    Card(
//                        modifier = Modifier,
//                        elevation = cardElevation(
//                            animateDpAsState(
//                                if(dismissState.dismissDirection != null)
//                                    4.dp else 0.dp
//                            ).value
//                        )
//                    ) { ListItem(item.username) }
//                }
//            )
//        }
//    }
//}
//
//@Composable
//private fun ListItem(text: String) {
//    Box(
//        Modifier
//            .fillMaxWidth()
//            .background(colorScheme.primaryContainer), Alignment.Center
//    ) {
//        Text(text, Modifier.padding(16.dp))
//    }
//}
