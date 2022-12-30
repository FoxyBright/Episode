package ru.rikmasters.gilty.mainscreen.presentation.ui.main.bottomsheets

//import androidx.compose.animation.core.*
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import ru.rikmasters.gilty.shared.R
//import ru.rikmasters.gilty.shared.shared.DividerBold
//import ru.rikmasters.gilty.shared.shared.GradientButton
//import ru.rikmasters.gilty.shared.shared.ScrollTimePicker
//import ru.rikmasters.gilty.shared.theme.base.GiltyTheme
//
//@Composable
//fun CalendarBS(
//    modifier: Modifier = Modifier,
//    state: Boolean = false,
//    callback: TimeBSCallback? = null
//) {
//    Box(
//        modifier
//            .background(MaterialTheme.colorScheme.primaryContainer)
//    ) {
//        DividerBold(
//            Modifier
//                .width(40.dp)
//                .align(Alignment.TopCenter)
//                .padding(vertical = 10.dp)
//                .clip(CircleShape)
//        )
//
//        Row(Modifier.fillMaxWidth()) {
//            ScrollTimePicker(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 122.dp, top = 50.dp),
//                mutableStateOf(START_TIME),
//                mutableStateOf(START_TIME)
//            ) { h, m -> time = "$h:$m" }
//        }
//        GradientButton(
//            Modifier
//                .align(Alignment.BottomCenter)
//                .padding(horizontal = 16.dp)
//                .padding(bottom = 32.dp),
//            stringResource(R.string.save_button), true
//        ) { callback?.onSaveClick(time) }
//    }
//}
//



//
//@Preview
//@Composable
//private fun TimeBSPreview() {
//    GiltyTheme {
//        TimeBS(state = true)
//    }
//}