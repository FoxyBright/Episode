package ru.rikmasters.gilty.shared.shared

import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.rikmasters.gilty.shared.R.drawable.ic_trash_can
import ru.rikmasters.gilty.shared.R.string.meeting_filter_delete_tag_label
import ru.rikmasters.gilty.shared.theme.base.GiltyTheme

@Preview
@Composable
private fun SwipeableRowBackPreview(){
    GiltyTheme {
        SwipeableRowBack()
    }
}

@Composable
fun SwipeableRowBack(modifier: Modifier = Modifier) {
    Column(
        modifier.padding(8.dp),
        Top, CenterHorizontally
    ) {
        Icon(
            painterResource(ic_trash_can),
            (null), Modifier, White
        )
        Text(
            stringResource(meeting_filter_delete_tag_label),
            Modifier.padding(),
            White, fontWeight = SemiBold,
            style = typography.labelSmall
        )
    }
}