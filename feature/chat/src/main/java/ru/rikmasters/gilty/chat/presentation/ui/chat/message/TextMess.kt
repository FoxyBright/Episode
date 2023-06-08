package ru.rikmasters.gilty.chat.presentation.ui.chat.message
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.MaterialTheme.colorScheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment.Companion.Center
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import ru.rikmasters.gilty.shared.R
//import ru.rikmasters.gilty.shared.common.extentions.TextFlow
//import ru.rikmasters.gilty.shared.common.extentions.TextFlowObstacleAlignment
//

// TODO для того чтобы текст обтекал таймер пришедшего сообщения добавить в будущем

//@Preview
//@Composable
//fun testMess() {
//    val text = "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, " +
//            "t, t, t, t, t, t, t, t, t, t, t, "
//    Box(
//        contentAlignment = Center,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(colorScheme.primary),
//    ) {
//        Surface(
//            shape = RoundedCornerShape(25.dp),
//            modifier = Modifier.padding(16.dp)
//        ) {
//            TextFlow(
//                text = text,
//                textAlign = TextAlign.Right,
//                obstacleAlignment = TextFlowObstacleAlignment.BottomEnd,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(12.dp)
//            ) {
//                Box(
//                    Modifier
//                        .size(50.dp, 12.dp)
//                        .background(Color.Red, CircleShape)
//                        .padding(12.dp, 8.dp)
//                )
//            }
//        }
//    }
//}