package ru.rikmasters.gilty.shared.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rikmasters.gilty.shared.R

@Preview
@Composable
private fun ErrorPReview() {
    Box(Modifier.fillMaxSize()) {
        ErrorConnection()
    }
}

@Composable
fun ErrorConnection(
    errorMessage: String = stringResource(
        R.string.bad_connection
    ),
) {
    Box(
        Modifier
            .fillMaxHeight(0.85f)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        BottomCenter
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xCC0E0E0E),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Row(
                Modifier.padding(16.dp),
                Start, CenterVertically
            ) {
                val size = with(LocalDensity.current) {
                    28.sp.toDp()
                }
                Icon(
                    painter = painterResource(
                        R.drawable.ic_bad_connection
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(size),
                    tint = White
                )
                Text(
                    text = errorMessage,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    style = typography.labelSmall.copy(
                        White, 15.sp,
                    ),
                )
            }
        }
    }
}

//fun ErrorToast(
//    text: String,
//){
//
//}